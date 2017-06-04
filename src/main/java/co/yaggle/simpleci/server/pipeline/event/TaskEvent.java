package co.yaggle.simpleci.server.pipeline.event;

public interface TaskEvent extends RunningPipelineEvent {

    String getTaskId();
}
