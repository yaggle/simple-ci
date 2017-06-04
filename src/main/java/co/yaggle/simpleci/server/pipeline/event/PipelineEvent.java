package co.yaggle.simpleci.server.pipeline.event;

import java.time.ZonedDateTime;

public interface PipelineEvent {

    ZonedDateTime getTimestamp();
}
