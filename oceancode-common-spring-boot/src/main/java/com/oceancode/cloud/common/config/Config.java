package com.oceancode.cloud.common.config;

public class Config {

    public interface App {
        String APP_MODE = "oc.app.mode";
        int MICROSERVICE = 1;
        String APP_MQ_TYPE = "oc.mq.type";
        String APP_AUTH_TOKEN_ENABLED = "oc.app.auth.token.enabled";
        String APP_API_PREFIX = "oc.api.strips";
        String APP_RESOURCE_PREFIX = "oc.resource.prefix";
    }

    public interface Permission {
        String PREFIX = "oc.permission.";

        String AUTHORITIES = "authorities";
        String OPERATOR = "operator";
    }

    public interface Cache {
        String SESSION_CACHE_KEY = "oc.session.cache.key";
        String SESSION_TOKEN_SECRET = "oc.session.token.secret";
        String SESSION_TOKEN_EXPIRE = "oc.session.token.expire";
        String SESSION_REFRESH_TOKEN_EXPIRE = "oc.session.refresh.token.expire";
    }
}
