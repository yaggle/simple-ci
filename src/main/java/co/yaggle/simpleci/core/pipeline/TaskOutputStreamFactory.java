package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.pipeline.event.PipelineEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskOutputChannel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TaskOutputStreamFactory {

    public TaskOutputStream forOutputChannel(TaskOutputChannel outputChannel) {
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
