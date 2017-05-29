package co.yaggle.simpleci.server.event;

import java.time.ZonedDateTime;

public interface PipelineEvent {

    ZonedDateTime getTimestamp();
}
