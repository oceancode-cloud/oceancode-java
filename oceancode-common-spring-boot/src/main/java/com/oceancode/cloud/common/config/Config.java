package com.oceancode.cloud.common.config;

public class Config {

    public interface App {
        String APP_MODE = "app.mode";
        String APP_MQ_TYPE = "app.mq.type";
        String APP_AUTH_TOKEN_ENABLED = "app.auth.token.enabled";
        String APP_API_PREFIX = "oc.api.strips";
        String APP_RESOURCE_PREFIX = "oc.resource.prefix";
    }

    public interface Permission {
        String PREFIX = "oc.permission.";

        String AUTHORITIES = "authorities";
        String OPERATOR = "operator";
    }
}
