package co.yaggle.simpleci.server.webhook.github;

import co.yaggle.simpleci.server.git.GitPushEvent;
import lombok.Data;

@Data
public class GitHubPushEvent {

    /**
     * Get the equivalent {@link GitPushEvent}.
     *
     * @param uuid this repository's unique ID
     * @return the equivalent {@link GitPushEvent}
     */
    public GitPushEvent toGitPushEvent(String uuid) {
        // TODO
        return null;
    }
}
