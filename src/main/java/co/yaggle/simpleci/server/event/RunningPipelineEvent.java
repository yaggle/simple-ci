package co.yaggle.simpleci.server.event;

public interface RunningPipelineEvent extends PipelineEvent {

    String getContainerId();
}
