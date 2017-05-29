package co.yaggle.simpleci.server.event.task;

import co.yaggle.simpleci.server.OutputChannel;
import co.yaggle.simpleci.server.event.TaskEvent;
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
public class TaskCommandOutputEvent implements TaskEvent {

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
    private final OutputChannel channel;

    @NonNull
    @Getter
    private final String characters;
}
