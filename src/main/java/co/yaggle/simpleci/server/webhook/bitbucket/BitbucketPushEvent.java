package co.yaggle.simpleci.server.webhook.bitbucket;

import co.yaggle.simpleci.server.git.GitPushEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Webhook payload for Bitbucket push events. (Probably don't need half the shit in here.)
 */
@Data
public class BitbucketPushEvent {
    /**
     * Get the equivalent {@link GitPushEvent}.
     *
     * @param uuid this repository's unique ID
     * @return the equivalent {@link GitPushEvent}
     */
    public GitPushEvent toGitPushEvent(String uuid) {
        return GitPushEvent
                .builder()
                .uuid(uuid)
                .targets(push.changes
                                 .stream()
                                 .filter(p -> p.getNewChange() != null)
                                 .map(p -> GitPushEvent.Target
                                         .builder()
                                         .branch(p.getNewChange().getName())
                                         .hash(p.getNewChange().getTarget().getHash())
                                         .build())
                                 .collect(Collectors.toList()))
                .build();
    }


    private BitbucketOwnerEntity actor;
    private BitbucketRepositoryEntity repository;
    private Push push;

    @Data
    public static class Push {
        private List<ChangeSet> changes;
    }

    @Data
    public static class ChangeSet {

        @JsonProperty("new")
        private Change newChange;

        @JsonProperty("old")
        private Change oldChange;

        private Links links;
        private Boolean created;
        private Boolean forced;
        private Boolean closed;
        private List<Commit> commits;
        private Boolean truncated;

        @Data
        public static class Change {
            private String type; // branch, tag, or annotated_tag
            private String name; // name of branch or tag
            private Target target;
            private Links links;

            @Data
            public static class Target {
                private String type;
                private String hash;
                private BitbucketOwnerEntity author;
                private String message;
                private ZonedDateTime date;
                private List<Parent> parents;
                private Links links;

                @Data
                public static class Parent {
                    private String type;
                    private String hash;
                    private Links links;

                    @Data
                    public static class Links {
                        private BitbucketLink self;
                        private BitbucketLink html;
                    }
                }

                @Data
                public static class Links {
                    private BitbucketLink self;
                    private BitbucketLink html;
                }
            }

            @Data
            public static class Links {
                private String self;
                private String commits;
                private String html;
            }
        }

        @Data
        public static class Links {
            private BitbucketLink html;
            private BitbucketLink diff;
            private BitbucketLink commits;
        }

        @Data
        public static class Commit {
            private String type;
            private String hash;
            private String message;
            private BitbucketOwnerEntity author;

            @Data
            private static class Links {
                private BitbucketLink self;
                private BitbucketLink html;
            }
        }
    }
}
