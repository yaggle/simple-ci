package co.yaggle.simpleci.server.webhook.gitlab;

import co.yaggle.simpleci.server.git.GitEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitLabWebhookService {

    public void onPush(String repositoryUuid, GitLabPushEvent push) {
        gitEventService.onPush(push.toGitPushEvent(repositoryUuid));
    }

    public void onPullRequest(String repositoryUuid, GitLabPullRequestEvent pullRequest) {
        gitEventService.onPullRequest(pullRequest.toGitPullRequestEvent(repositoryUuid));
    }

    private final GitEventService gitEventService;
}
