package co.yaggle.simpleci.server.pipeline.event;

public interface RunningPipelineEvent extends PipelineEvent {

    String getContainerId();
}
