package co.yaggle.simpleci.core.pipeline.event;

public interface RunningPipelineEvent extends PipelineEvent {

    String getContainerId();
}
