package co.yaggle.simpleci.core.pipeline.event;

import java.time.ZonedDateTime;

public interface PipelineEvent {

    ZonedDateTime getTimestamp();
}
