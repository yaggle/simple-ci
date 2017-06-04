package co.yaggle.simpleci.core.pipeline.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline task's command has started.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskCommandStartedEvent implements TaskEvent {

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
}
