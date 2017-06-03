package co.yaggle.simpleci.server.webhook.gitlab;

import co.yaggle.simpleci.server.git.GitPushEvent;
import lombok.Data;

@Data
public class GitLabPushEvent {
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
