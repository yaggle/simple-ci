package co.yaggle.simpleci.core.pipeline.event;

public interface RunningTaskEvent extends TaskEvent {

    String getContainerId();
}
