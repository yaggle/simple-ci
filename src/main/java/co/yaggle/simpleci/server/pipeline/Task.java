package co.yaggle.simpleci.server.pipeline;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.StringUtils.*;

@Getter
@EqualsAndHashCode(of = "id")
public class Task {

    @Builder
    private Task(String id, String name, String branch, List<String> commands, List<Task> nextTasks) {
        checkArgument(!isBlank(id));
        checkArgument(!isBlank(name));

        this.id = id;
        this.name = name;
        this.branch = branch;
        this.commands = ImmutableList.copyOf(checkNotNull(commands));
        this.nextTasks = ImmutableList.copyOf(checkNotNull(nextTasks));
    }

    private String id;

    private String name;

    private String branch;

    private List<String> commands;

    private List<Task> nextTasks;
}
