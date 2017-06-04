package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.ContainerClient;
import co.yaggle.simpleci.core.pipeline.event.ContainerStartedEvent;
import co.yaggle.simpleci.core.pipeline.event.ImageLoadFailedEvent;
import co.yaggle.simpleci.core.pipeline.event.PipelineEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandAbortedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandCompletedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandStartedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskOutputChannel;
import com.spotify.docker.client.exceptions.DockerException;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class PipelineRunner {

    /**
     * <p>
     * Launch a build pipeline in a new container.
     * </p>
     * <p>
     * This will return immediately, leaving the caller to monitor progress
     * via events published in the event queue.
     * </p>
     *
     * @param pipeline           a build pipeline
     * @param mountFromDirectory the absolute path to the local directory to mount
     * @param eventQueue         an event queue into which task-related events will be published
     */
    public void launchPipeline(Pipeline pipeline, File mountFromDirectory, BlockingQueue<PipelineEvent> eventQueue) {

        // Start a container from the pipeline's image ID.
        final String containerId;
        try {
            containerId = containerClient.createContainer(pipeline.getImage(), mountFromDirectory.getCanonicalPath(), pipeline.getVolume());
        } catch (DockerException e) {
            eventQueue.add(ImageLoadFailedEvent
                                   .builder()
                                   .timestamp(ZonedDateTime.now())
                                   .message(e.getMessage())
                                   .build());
            return;
        } catch (InterruptedException e) {
            eventQueue.add(ImageLoadFailedEvent
                                   .builder()
                                   .timestamp(ZonedDateTime.now())
                                   .message("Thread interrupted while loading Docker image")
                                   .build());
            return;
        } catch (IOException e) {
            eventQueue.add(ImageLoadFailedEvent
                                   .builder()
                                   .timestamp(ZonedDateTime.now())
                                   .message("Cannot get canonical path of project root directory")
                                   .build());
            return;
        }

        eventQueue.add(ContainerStartedEvent
                               .builder()
                               .containerId(containerId)
                               .timestamp(ZonedDateTime.now())
                               .build());

        launchTasks(containerId, pipeline.getTasks(), eventQueue, ConcurrentHashMap.newKeySet());

        // TODO: Ensure the container is shutdown when no more tasks remain.
    }


    /**
     * <p>
     * Recursively run a list of tasks in parallel, and their next tasks, and so on.
     * </p>
     * <p>
     * This will return immediately, leaving the caller to monitor progress
     * via events published in the event queue.
     * </p>
     *
     * @param containerId    the ID of the container in which the tasks will run
     * @param tasks          the list of tasks to run in parallel
     * @param eventQueue     an event queue into which task-related events will be published
     * @param startedTaskIds a concurrent set to track IDs of tasks already started so that they aren't started more than once
     */
    private void launchTasks(String containerId, List<Task> tasks, BlockingQueue<PipelineEvent> eventQueue, Set<String> startedTaskIds) {
        for (Task task : tasks) {

            // Atomically (because the Set is concurrent) check if the task is already running and, if not, register it as running and run it.
            if (startedTaskIds.add(task.getId())) {
                new Thread(() -> {

                    // Run all the task's commands sequentially, bailing out on the first failure.
                    for (int i = 0; i < task.getCommands().size(); ++i) {
                        if (runTaskCommand(containerId, task, i, eventQueue) != 0) {
                            return;
                        }
                    }

                    // Recursively run this task's next tasks
                    if (!task.getNextTasks().isEmpty()) {
                        launchTasks(containerId, task.getNextTasks(), eventQueue, startedTaskIds);
                    }
                }).start();
            }
        }
    }


    /**
     * Run a task's command.
     *
     * @param containerId  the ID of a container
     * @param task         a task
     * @param commandIndex the index of a command within the task to run
     * @param eventQueue   an event queue to publish task events as they occur
     * @return the command's exit code
     */
    private int runTaskCommand(String containerId, Task task, int commandIndex, BlockingQueue<PipelineEvent> eventQueue) {
        TaskOutputStreamFactory taskOutputStreamFactory = TaskOutputStreamFactory
                .builder()
                .containerId(containerId)
                .taskId(task.getId())
                .commandIndex(commandIndex)
                .eventQueue(eventQueue)
                .build();

        TaskOutputStream stdoutStream = taskOutputStreamFactory.forOutputChannel(TaskOutputChannel.STDOUT);
        TaskOutputStream stderrStream = taskOutputStreamFactory.forOutputChannel(TaskOutputChannel.STDERR);

        eventQueue.add(TaskCommandStartedEvent
                               .builder()
                               .timestamp(ZonedDateTime.now())
                               .containerId(containerId)
                               .taskId(task.getId())
                               .commandIndex(commandIndex)
                               .command(task.getCommands().get(commandIndex))
                               .build());

        try {
            String execId = containerClient.execCommand(containerId, task.getCommands().get(commandIndex), stdoutStream, stderrStream);
            int exitCode = containerClient.getCommandExitCode(execId);
            eventQueue.add(TaskCommandCompletedEvent
                                   .builder()
                                   .timestamp(ZonedDateTime.now())
                                   .containerId(containerId)
                                   .taskId(task.getId())
                                   .commandIndex(commandIndex)
                                   .exitCode(exitCode)
                                   .build());
            return exitCode;
        } catch (DockerException | InterruptedException | IOException e) {
            eventQueue.add(TaskCommandAbortedEvent
                                   .builder()
                                   .timestamp(ZonedDateTime.now())
                                   .containerId(containerId)
                                   .taskId(task.getId())
                                   .commandIndex(commandIndex)
                                   .errorSource(TaskCommandAbortedEvent.ErrorSource.forException(e))
                                   .errorMessage(e.getMessage())
                                   .build());
            return -1;
        }
    }


    private final ContainerClient containerClient;
}
