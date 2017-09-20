package co.yaggle.simpleci.core;

import co.yaggle.simpleci.core.pipeline.TaskOutputStream;
import co.yaggle.simpleci.core.pipeline.event.PipelineEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandCompletedEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandOutputEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskOutputChannel;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.HostConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import static java.util.Collections.*;

public class DockerCommandRunnerExample {

    /**
     * {@link DockerCommandRunner} demo (temporary)
     *
     * @param args args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        BlockingQueue<PipelineEvent> eventQueue = new LinkedBlockingQueue<>();

        Function<String, OutputStream> stdout = containerId -> TaskOutputStream
                .builder()
                .containerId(containerId)
                .taskId("1")
                .commandIndex(0)
                .outputChannel(TaskOutputChannel.STDOUT)
                .eventQueue(eventQueue)
                .build();

        Function<String, OutputStream> stderr = containerId -> TaskOutputStream
                .builder()
                .containerId(containerId)
                .taskId("1")
                .commandIndex(0)
                .outputChannel(TaskOutputChannel.STDERR)
                .eventQueue(eventQueue)
                .build();

        // Run command in background
        new Thread(() -> {
            try {
                int exitCode = DockerCommandRunner
                        .builder()
                        .image("alpine:3.6")
                        .binds(singletonList(HostConfig.Bind
                                                     .from("/Users/steve/simple-ci-test")
                                                     .to("/root/simple-ci")
                                                     .readOnly(false)
                                                     .build()))
                        .build()
                        .runCommand(
                            "echo \"Hello :)\"",
                            stdout,
                            stderr,
                            containerId -> System.out.println(String.format("Container %s created", containerId)),
                            containerId -> System.out.println(String.format("Container %s started", containerId)),
                            containerId -> System.out.println(String.format("Container %s stopped", containerId)),
                            containerId -> System.out.println(String.format("Container %s deleted", containerId))
                        )
                        .getExitCode();

                eventQueue.add(TaskCommandCompletedEvent
                                       .builder()
                                       .containerId("???")
                                       .commandIndex(0)
                                       .taskId("???")
                                       .timestamp(ZonedDateTime.now())
                                       .exitCode(exitCode)
                                       .build());

                System.out.println("Command completed");

            } catch (DockerException | InterruptedException | IOException | DockerCertificateException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Process the event queue
        PipelineEvent pipelineEvent = eventQueue.take();
        while (!(pipelineEvent instanceof TaskCommandCompletedEvent)) {
            if (pipelineEvent instanceof TaskCommandOutputEvent) {
                TaskCommandOutputEvent output = (TaskCommandOutputEvent) pipelineEvent;
                if (output.getChannel() == TaskOutputChannel.STDOUT) {
                    System.out.print(output.getCharacters());
                } else if (output.getChannel() == TaskOutputChannel.STDERR) {
                    System.err.print(output.getCharacters());
                }
            }

            pipelineEvent = eventQueue.take();
        }

        System.out.println("\nFinished processing events.");
    }
}
