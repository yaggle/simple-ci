package co.yaggle.simpleci.server.webhook.bitbucket;

import co.yaggle.simpleci.server.git.GitPullRequestEvent;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Webhook payload for Bitbucket pull request events. (Probably don't need half the shit in here.)
 */
@Data
public class BitbucketPullRequestEvent {
    /**
     * Get the equivalent {@link GitPullRequestEvent}.
     *
     * @param uuid this repository's unique ID
     * @return the equivalent {@link GitPullRequestEvent}
     */
    public GitPullRequestEvent toGitPullRequestEvent(String uuid) {
        return GitPullRequestEvent
                .builder()
                .uuid(uuid)
                .pullRequestId(getId())
                .branch(getSource().getBranch().getName())
                .hash(getSource().getCommit().getHash())
                .build();
    }


    private Long id;
    private String title;
    private String description;
    private State state;
    private BitbucketOwnerEntity author;
    private SourceDest source;
    private SourceDest destination;
    private MergeCommit merge_commit;
    private List<BitbucketOwnerEntity> participants;
    private List<BitbucketOwnerEntity> reviewers;
    private Boolean close_source_branch;
    private BitbucketOwnerEntity closed_by;
    private String reason;
    private ZonedDateTime created_on;
    private ZonedDateTime updated_on;
    private Links links;

    public enum State {
        OPEN,
        MERGED,
        DECLINED,
    }

    @Data
    public static class SourceDest {
        private Branch branch;
        private Commit commit;
        private BitbucketRepositoryEntity repository;

        @Data
        public static class Branch {
            private String name;
        }

        @Data
        public static class Commit {
            private String hash;
        }
    }

    @Data
    public static class MergeCommit {
        private String hash;
    }

    @Data
    public static class Links {
        private BitbucketLink self;
        private BitbucketLink html;
    }
}
