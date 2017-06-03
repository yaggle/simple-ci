package co.yaggle.simpleci.server.webhook.github;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhook/github")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GitHubWebhookController {

    @PostMapping(path = "/{repositoryUuid}")
    public void handlePush(@PathVariable String repositoryUuid, @RequestBody GitHubPushEvent request) {
        githubWebhookService.onPush(repositoryUuid, request);
    }


    @PostMapping(path = "/{repositoryUuid}")
    public void handlePullRequest(@PathVariable String repositoryUuid, @RequestBody GitHubPullRequestEvent request) {
        githubWebhookService.onPullRequest(repositoryUuid, request);
    }


    private final GitHubWebhookService githubWebhookService;
}
