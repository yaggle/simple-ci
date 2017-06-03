package co.yaggle.simpleci.server.webhook.bitbucket;

import lombok.Data;

@Data
public class BitbucketRepositoryEntity {
    private String type;
    private String name;
    private String full_name;
    private String uuid;
    private Links links;
    private BitbucketProjectEntity project;
    private String website;
    private BitbucketOwnerEntity owner;
    private String scm;
    private Boolean is_private;

    @Data
    public static class Links {
        private BitbucketLink self;
        private BitbucketLink html;
        private BitbucketLink avatar;
    }
}
