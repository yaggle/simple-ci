package co.yaggle.simpleci.server.pipeline.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline's Docker container has started.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContainerStartedEvent implements RunningPipelineEvent {

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final ZonedDateTime timestamp;

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final String containerId;
}
