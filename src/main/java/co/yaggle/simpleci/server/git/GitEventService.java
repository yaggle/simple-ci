package co.yaggle.simpleci.server.git;

import org.springframework.stereotype.Service;

@Service
public class GitEventService {

    public void onPush(GitPushEvent push) {
        // TODO: Get git repo details from the database, clone, checkout commit, then start the build pipeline.
        // Note: This should be done in a different thread so the webhook doesn't time out
    }

    public void onPullRequest(GitPullRequestEvent pullRequest) {
        // TODO: Get git repo details from the database, clone, checkout commit, then start the build pipeline.
        // Note: This should be done in a different thread so the webhook doesn't time out
    }
}
