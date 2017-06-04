package co.yaggle.simpleci.core.pipeline.event;

public interface TaskEvent extends RunningPipelineEvent {

    String getTaskId();
}
