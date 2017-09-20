package co.yaggle.simpleci.core.pipeline.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline's task emits output to
 * <code>stdout</code> or <code>stderr</code>.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskCommandOutputEvent implements RunningTaskEvent {

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
    private final TaskOutputChannel channel;

    @NonNull
    @Getter
    private final String characters;
}
