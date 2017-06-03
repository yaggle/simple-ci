package co.yaggle.simpleci.server.webhook.github;

import co.yaggle.simpleci.server.git.GitEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitHubWebhookService {

    public void onPush(String repositoryUuid, GitHubPushEvent push) {
        gitEventService.onPush(push.toGitPushEvent(repositoryUuid));
    }

    public void onPullRequest(String repositoryUuid, GitHubPullRequestEvent pullRequest) {
        gitEventService.onPullRequest(pullRequest.toGitPullRequestEvent(repositoryUuid));
    }

    private final GitEventService gitEventService;
}
