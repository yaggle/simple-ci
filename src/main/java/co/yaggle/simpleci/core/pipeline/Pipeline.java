package co.yaggle.simpleci.core.pipeline;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.StringUtils.*;

@Getter
public class Pipeline {

    @Builder
    private Pipeline(String image, List<Task> tasks) {
        checkArgument(!isBlank(image));

        this.image = image;
        this.tasks = ImmutableList.copyOf(checkNotNull(tasks));
    }

    private String image;

    /** The starting tasks (not necessarily all the tasks) */
    private List<Task> tasks;
}
