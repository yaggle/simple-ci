package co.yaggle.simpleci.server.webhook.bitbucket;

import lombok.Data;

@Data
public class BitbucketProjectEntity {
    private String type;
    private String name;
    private String uuid;
    private Links links;
    private String key;

    @Data
    public static class Links {
        private BitbucketLink html;
        private BitbucketLink avatar;
    }
}
