package co.yaggle.simpleci.server.pipeline.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@Builder
public class TaskElement {

    @NonNull
    private String id;

    @NonNull
    private String name;

    private List<String> dependsOn;

    private String branch;

    private List<String> commands;
}
