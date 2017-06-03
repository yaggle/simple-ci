package co.yaggle.simpleci.server.webhook.bitbucket;

import lombok.Data;

@Data
public class BitbucketOwnerEntity {
    private String type;
    private String username;
    private String display_name;
    private String uuid;
    private Links links;

    @Data
    public static class Links {
        private BitbucketLink self;
        private BitbucketLink html;
        private BitbucketLink avatar;
    }
}
