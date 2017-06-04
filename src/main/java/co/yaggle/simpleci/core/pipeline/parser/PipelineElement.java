package co.yaggle.simpleci.core.pipeline.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@Builder
public class PipelineElement {

    @NonNull
    private String image;

    @NonNull
    private String volume;

    @NonNull
    private List<TaskElement> tasks;
}
