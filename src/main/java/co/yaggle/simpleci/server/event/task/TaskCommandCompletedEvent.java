package co.yaggle.simpleci.server.event.task;

import co.yaggle.simpleci.server.event.TaskEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline's task has completed.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskCommandCompletedEvent implements TaskEvent {

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final ZonedDateTime timestamp;

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final String containerId;

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final String taskId;

    @NonNull
    @Getter
    private final Integer commandIndex;

    @NonNull
    @Getter
    private final Integer exitCode;
}
