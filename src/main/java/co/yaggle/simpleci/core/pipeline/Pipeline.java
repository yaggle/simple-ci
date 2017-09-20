package co.yaggle.simpleci.core.pipeline;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.StringUtils.*;

@Getter
public class Pipeline {

    @Builder
    private Pipeline(String image, String volume, List<Task> tasks) {
        checkArgument(!isBlank(image));
        checkArgument(!isBlank(volume));

        this.image = image;
        this.volume = volume;
        this.tasks = ImmutableList.copyOf(checkNotNull(tasks));
    }


    /**
     * Get all the tasks in the pipeline, not just the start tasks.
     *
     * @return all the pipeline's tasks
     */
    public List<Task> getAllTasks() {
        List<Task> result = new ArrayList<>();
        accumulateAllTasks(tasks, result, new HashSet<>());
        return result;
    }


    private void accumulateAllTasks(List<Task> tasks, List<Task> allTasks, Set<String> taskIds) {
        for (Task task : tasks) {
            if (taskIds.add(task.getId())) {
                allTasks.add(task);
                accumulateAllTasks(task.getNextTasks(), allTasks, taskIds);
            }
        }
    }


    private String image;

    private String volume;

    /**
     * The starting tasks (not necessarily all the tasks)
     */
    private List<Task> tasks;
}
