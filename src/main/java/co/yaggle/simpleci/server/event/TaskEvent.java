package co.yaggle.simpleci.server.event;

public interface TaskEvent extends RunningPipelineEvent {

    String getTaskId();
}
