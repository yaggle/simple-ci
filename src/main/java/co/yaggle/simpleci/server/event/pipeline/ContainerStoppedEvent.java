package co.yaggle.simpleci.server.event.pipeline;

import co.yaggle.simpleci.server.event.RunningPipelineEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline's Docker container has stopped.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContainerStoppedEvent implements RunningPipelineEvent {

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final ZonedDateTime timestamp;

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final String containerId;
}
