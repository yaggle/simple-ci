package co.yaggle.simpleci.server.webhook.gitlab;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook/gitlab")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitLabWebhookController {

    @PostMapping(path = "/{repositoryUuid}")
    public void handlePush(@PathVariable String repositoryUuid, @RequestBody GitLabPushEvent request) {
        gitLabWebhookService.onPush(repositoryUuid, request);
    }


    @PostMapping(path = "/{repositoryUuid}")
    public void handlePullRequest(@PathVariable String repositoryUuid, @RequestBody GitLabPullRequestEvent request) {
        gitLabWebhookService.onPullRequest(repositoryUuid, request);
    }


    private final GitLabWebhookService gitLabWebhookService;
}
