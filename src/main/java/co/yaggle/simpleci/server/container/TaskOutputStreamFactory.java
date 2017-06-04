package co.yaggle.simpleci.server.container;

import co.yaggle.simpleci.server.OutputChannel;
import co.yaggle.simpleci.server.pipeline.event.PipelineEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TaskOutputStreamFactory {

    public TaskOutputStream forOutputChannel(OutputChannel outputChannel) {
        return TaskOutputStream
                .builder()
                .containerId(containerId)
                .taskId(taskId)
                .commandIndex(commandIndex)
                .outputChannel(outputChannel)
                .eventQueue(eventQueue)
                .build();
    }

    @NonNull
    private final String containerId;

    @NonNull
    private final String taskId;

    @NonNull
    private final Integer commandIndex;

    @NonNull
    private final BlockingQueue<PipelineEvent> eventQueue;
}
