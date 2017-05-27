package co.yaggle.simpleci.server.converter;

import co.yaggle.simpleci.server.exception.CyclicTaskDependencyException;
import co.yaggle.simpleci.server.exception.DuplicateDependencyException;
import co.yaggle.simpleci.server.model.Task;
import co.yaggle.simpleci.server.parser.TaskElement;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskElementsToTasksConverter {

    /**
     * Given a flat list of task elements from the project's build pipeline configuration,
     * return a list of the first tasks to execute in parallel, each pointing to their next
     * tasks.
     *
     * @param taskElements the flat list of task elements from the build pipeline configuration
     * @return the first tasks to execute in parallel, with references to subsequent tasks
     * @throws CyclicTaskDependencyException if a cyclic dependency is detected
     * @throws DuplicateDependencyException if a task element declares multiple dependencies on the same task
     */
    public static List<Task> taskElementsToTasks(List<TaskElement> taskElements) throws CyclicTaskDependencyException, DuplicateDependencyException {

        // Fail fast if any tasks reference themselves as a dependency.
        if (taskElements.stream().anyMatch(taskElement -> taskElement.getDependsOn().contains(taskElement.getId()))) {
            throw new CyclicTaskDependencyException();
        }

        Map<String, TaskElement> unprocessedTaskElementsById = taskElements
                .stream()
                .collect(Collectors.toMap(TaskElement::getId, Function.identity()));

        Multimap<String, Task> processedTasksByPreviousTaskId = LinkedListMultimap.create();

        List<Task> tasks = new ArrayList<>();

        do {
            // Get all the unprocessed tasks that aren't depended on by any other unprocessed tasks (i.e. work backwards)
            List<TaskElement> elementsToProcess = unprocessedTaskElementsById
                    .values()
                    .stream()
                    .filter(taskElement -> unprocessedTaskElementsById
                            .values()
                            .stream()
                            .noneMatch(otherTaskElement -> otherTaskElement.getDependsOn().contains(taskElement.getId())))
                    .collect(Collectors.toList());

            // If there aren't any such elements, despite there being unprocessed tasks, there must be a cyclic dependency.
            if (elementsToProcess.isEmpty()) {
                throw new CyclicTaskDependencyException();
            }

            elementsToProcess.forEach(taskElement -> {
                Task task = Task
                        .builder()
                        .id(taskElement.getId())
                        .name(taskElement.getName())
                        .branch(taskElement.getBranch())
                        .commands(taskElement.getCommands())
                        .nextTasks(new ArrayList<>(processedTasksByPreviousTaskId.get(taskElement.getId())))
                        .build();

                // Is this one of the set of first tasks in the pipeline?
                if (taskElement.getDependsOn().isEmpty()) {
                    // This is a starting task, so include it in the list of tasks to return.
                    tasks.add(task);
                } else {
                    // This isn't a starting task, so register all its previous task IDs.
                    taskElement.getDependsOn().forEach(previousTaskId -> processedTasksByPreviousTaskId.put(previousTaskId, task));
                }

                // Remove this task element from the unprocessed list.
                unprocessedTaskElementsById.remove(taskElement.getId());
            });
        } while (!unprocessedTaskElementsById.isEmpty());

        return tasks;
    }
}
