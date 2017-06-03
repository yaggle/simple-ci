package co.yaggle.simpleci.server.webhook.bitbucket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook/bitbucket")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BitbucketWebhookController {

    @PostMapping(path = "/{repositoryUuid}")
    public void handlePush(@PathVariable String repositoryUuid, @RequestBody BitbucketPushEvent request) {
        bitbucketWebhookService.onPush(repositoryUuid, request);
    }


    @PostMapping(path = "/{repositoryUuid}")
    public void handlePullRequest(@PathVariable String repositoryUuid, @RequestBody BitbucketPullRequestEvent request) {
        bitbucketWebhookService.onPullRequest(repositoryUuid, request);
    }


    private final BitbucketWebhookService bitbucketWebhookService;
}
