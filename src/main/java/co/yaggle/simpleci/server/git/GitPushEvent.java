package co.yaggle.simpleci.server.git;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GitPushEvent {
    private String uuid;
    private List<Target> targets;

    @Value
    @Builder
    public static class Target {
        private String branch;
        private String hash;
    }
}
