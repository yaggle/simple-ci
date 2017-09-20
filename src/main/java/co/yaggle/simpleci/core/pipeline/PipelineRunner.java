package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.DockerCommandRunner;
import co.yaggle.simpleci.core.pipeline.event.ContainerCreatedEvent;
import co.yaggle.simpleci.core.pipeline.event.ContainerDeletedEvent;
import co.yaggle.simpleci.core.pipeline.event.ContainerStartedEvent;
import co.yaggle.simpleci.core.pipeline.event.ContainerStoppedEvent;
import co.yaggle.simpleci.core.pipeline.event.ImageLoadFailedEvent;
import co.yaggle.simpleci.core.pipeline.event.PipelineCompletedEvent;
import co.yaggle.simpleci.core.pipeline.event.PipelineEvent;
import co.yaggle.simpleci.core.pipeline.event.PipelineStartedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandAbortedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandCompletedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandStartedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCompletedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskOutputChannel;
import co.yaggle.simpleci.core.pipeline.event.TaskStartedEvent;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.HostConfig;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PipelineRunner {

    /**
     * Run a pipeline.
     *
     * @param pipeline the pipeline to launch
     */
    public void run(Pipeline pipeline) {
        try {
            val dockerCommandRunner = DockerCommandRunner
                .builder()
                .image(pipeline.getImage())
                .binds(
                    singletonList(
                        HostConfig.Bind
                            .from(mountFromDirectory.getCanonicalPath())
                            .to(pipeline.getVolume())
                            .readOnly(false)
                            .build()
                    )
                )
                .build();

            eventQueue.add(
                PipelineStartedEvent
                    .builder()
                    .timestamp(ZonedDateTime.now())
                    .build()
            );

            val allTaskIds = pipeline
                .getAllTasks()
                .stream()
                .map(Task::getId)
                .collect(toCollection(ConcurrentHashMap::newKeySet));

            val completedTaskIds = ConcurrentHashMap.newKeySet();

            launchTasks(
                dockerCommandRunner,
                pipeline.getTasks(),
                ConcurrentHashMap.newKeySet(),
                taskId -> {
                    completedTaskIds.add(taskId);
                    if (completedTaskIds.equals(allTaskIds)) {
                        eventQueue.add(
                            PipelineCompletedEvent
                                .builder()
                                .timestamp(ZonedDateTime.now())
                                .build()
                        );
                    }
                }
            );

        } catch (DockerException | DockerCertificateException e) {
            eventQueue.add(ImageLoadFailedEvent
                               .builder()
                               .timestamp(ZonedDateTime.now())
                               .message(e.getMessage())
                               .build());
        } catch (InterruptedException e) {
            eventQueue.add(ImageLoadFailedEvent
                               .builder()
                               .timestamp(ZonedDateTime.now())
                               .message("Thread interrupted while loading Docker image")
                               .build());
        } catch (IOException e) {
            eventQueue.add(ImageLoadFailedEvent
                               .builder()
                               .timestamp(ZonedDateTime.now())
                               .message("Cannot get canonical path of project root directory")
                               .build());
        }
    }


    private void launchTasks(DockerCommandRunner dockerCommandRunner, List<Task> tasks, Set<String> startedTaskIds, Consumer<String> onTaskCompleted) {
        for (var task : tasks) {

            // Atomically (because the Set is concurrent) check if the task is already running and, if not, register it as running and run it.
            if (startedTaskIds.add(task.getId())) {
                new Thread(() -> {
                    eventQueue.add(
                        TaskStartedEvent
                            .builder()
                            .taskId(task.getId())
                            .timestamp(ZonedDateTime.now())
                            .build()
                    );

                    // Run all the task's commands sequentially, bailing out on the first failure.
                    for (int i = 0; i < task.getCommands().size(); ++i) {
                        if (runTaskCommand(dockerCommandRunner, task, i) != 0) {
                            return;
                        }
                    }

                    eventQueue.add(
                        TaskCompletedEvent
                            .builder()
                            .taskId(task.getId())
                            .timestamp(ZonedDateTime.now())
                            .build()
                    );

                    onTaskCompleted.accept(task.getId());

                    // Recursively run this task's next tasks
                    if (!task.getNextTasks().isEmpty()) {
                        launchTasks(dockerCommandRunner, task.getNextTasks(), startedTaskIds, onTaskCompleted);
                    }
                }).start();
            }
        }
    }


    private int runTaskCommand(DockerCommandRunner dockerCommandRunner, Task task, int commandIndex) {
        Function<String, OutputStream> stdout = containerId -> TaskOutputStream
            .builder()
            .containerId(containerId)
            .taskId(task.getId())
            .commandIndex(commandIndex)
            .outputChannel(TaskOutputChannel.STDOUT)
            .eventQueue(eventQueue)
            .build();

        Function<String, OutputStream> stderr = containerId -> TaskOutputStream
            .builder()
            .containerId(containerId)
            .taskId(task.getId())
            .commandIndex(commandIndex)
            .outputChannel(TaskOutputChannel.STDERR)
            .eventQueue(eventQueue)
            .build();

        try {
            val result = dockerCommandRunner.runCommand(
                task.getCommands().get(commandIndex),
                stdout,
                stderr,
                containerId -> {
                    eventQueue.add(
                        TaskCommandStartedEvent
                            .builder()
                            .containerId(containerId)
                            .taskId(task.getId())
                            .commandIndex(commandIndex)
                            .command(task.getCommands().get(commandIndex))
                            .timestamp(ZonedDateTime.now())
                            .build()
                    );
                    eventQueue.add(
                        ContainerCreatedEvent
                            .builder()
                            .containerId(containerId)
                            .timestamp(ZonedDateTime.now())
                            .build()
                    );
                },
                containerId -> eventQueue.add(
                    ContainerStartedEvent
                        .builder()
                        .containerId(containerId)
                        .timestamp(ZonedDateTime.now())
                        .build()
                ),
                containerId -> eventQueue.add(
                    ContainerStoppedEvent
                        .builder()
                        .containerId(containerId)
                        .timestamp(ZonedDateTime.now())
                        .build()
                ),
                containerId -> eventQueue.add(
                    ContainerDeletedEvent
                        .builder()
                        .containerId(containerId)
                        .timestamp(ZonedDateTime.now())
                        .build()
                )
            );

            eventQueue.add(TaskCommandCompletedEvent
                               .builder()
                               .containerId(result.getContainerId())
                               .commandIndex(commandIndex)
                               .taskId(task.getId())
                               .timestamp(ZonedDateTime.now())
                               .exitCode(result.getExitCode())
                               .build());

            return result.getExitCode();

        } catch (DockerException | InterruptedException | IOException e) {
            eventQueue.add(TaskCommandAbortedEvent
                               .builder()
                               .timestamp(ZonedDateTime.now())
                               .taskId(task.getId())
                               .commandIndex(commandIndex)
                               .errorSource(TaskCommandAbortedEvent.ErrorSource.forException(e))
                               .errorMessage(e.getMessage())
                               .build());
            return -1;
        }
    }


    private final Pipeline pipeline;
    private final File mountFromDirectory;
    private final BlockingQueue<PipelineEvent> eventQueue;
}
