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
    private Pipeline(String image, String volume, List<Task> tasks) {
        checkArgument(!isBlank(image));
        checkArgument(!isBlank(volume));

        this.image = image;
        this.volume = volume;
        this.tasks = ImmutableList.copyOf(checkNotNull(tasks));
    }

    private String image;

    private String volume;

    /** The starting tasks (not necessarily all the tasks) */
    private List<Task> tasks;
}
