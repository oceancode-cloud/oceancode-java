package com.oceancode.cloud.common.config;

public class Config {

    public interface App {
        String APP_MODE = "app.mode";
        String APP_MQ_TYPE = "app.mq.type";
        String APP_AUTH_TOKEN_ENABLED = "app.auth.token.enabled";
        String APP_API_PREFIX = "app.api.prefix";
        String APP_RESOURCE_PREFIX = "app.resource.prefix";
    }
}
