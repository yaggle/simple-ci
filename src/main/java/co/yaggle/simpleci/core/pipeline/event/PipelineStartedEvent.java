package co.yaggle.simpleci.core.pipeline.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PipelineStartedEvent implements PipelineEvent {

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final ZonedDateTime timestamp;
}
