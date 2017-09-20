package co.yaggle.simpleci.core.pipeline;

import org.junit.Test;

import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PipelineTest {

    @Test
    public void testGetAllTasks() {
        Pipeline pipeline = Pipeline
            .builder()
            .image("node")
            .volume("/root")
            .tasks(
                singletonList(
                    Task
                        .builder()
                        .id("hello")
                        .name("Hello")
                        .commands(singletonList("echo \"hello\""))
                        .nextTasks(
                            singletonList(
                                Task
                                    .builder()
                                    .id("goodbye")
                                    .name("Goodbye")
                                    .commands(singletonList("echo \"goodbye\""))
                                    .nextTasks(emptyList())
                                    .build()
                            )
                        )
                        .build()
                )
            )
            .build();

        assertThat(pipeline.getAllTasks().size(), is(2));
        assertTrue(pipeline.getAllTasks().stream().anyMatch(task -> task.getId().equals("hello")));
        assertTrue(pipeline.getAllTasks().stream().anyMatch(task -> task.getId().equals("goodbye")));
    }
}
