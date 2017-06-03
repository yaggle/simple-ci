package co.yaggle.simpleci.server.git;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GitPullRequestEvent {
    private String uuid;
    private Long pullRequestId;
    private String branch;
    private String hash;
}
