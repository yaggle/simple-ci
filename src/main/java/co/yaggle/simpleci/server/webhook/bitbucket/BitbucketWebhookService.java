package co.yaggle.simpleci.server.webhook.bitbucket;

import co.yaggle.simpleci.server.git.GitEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BitbucketWebhookService {

    public void onPush(String repositoryUuid, BitbucketPushEvent push) {
        gitEventService.onPush(push.toGitPushEvent(repositoryUuid));
    }


    public void onPullRequest(String repositoryUuid, BitbucketPullRequestEvent pullRequest) {
        gitEventService.onPullRequest(pullRequest.toGitPullRequestEvent(repositoryUuid));
    }


    private final GitEventService gitEventService;
}
