package co.yaggle.simpleci.server.pipeline.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline's Docker image has been loaded.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageLoadedEvent implements PipelineEvent {

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final ZonedDateTime timestamp;
}
