package com.oceancode.cloud.api.oauth;

import com.oceancode.cloud.api.account.Account;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Map;

public class OauthorizeAccount extends Account {
    private String scope;

    public OauthorizeAccount(Map<String, Object> config) {
        super(config);
    }

    public String getUrl() {
        return (String) getConfig().get("url");
    }

    public String getClientId() {
        return (String) getConfig().get("clientId");
    }

    public String getRedirectUrl() {
        return (String) getConfig().get("redirectUrl");
    }

    public String getResponseType() {
        String type = (String) getConfig().get("responseType");
        if (ValueUtil.isEmpty(type)) {
            return "code";
        }
        return type;
    }

    public String getState() {
        return (String) getConfig().get("state");
    }

    public String getScope() {
        return (String) getConfig().get("scope");
    }

    public String getClientSecret() {
        return (String) getConfig().get("clientSecret");
    }

    public String getAccessTokenUrl() {
        return (String) getConfig().get("accessTokenUrl");
    }

    public String getHomeUrl() {
        return (String) getConfig().get("homeUrl");
    }

    public String getRefreshToken() {
        return (String) getConfig().get("refreshToken");
    }

    public String getUserInfoUrl() {
        return (String) getConfig().get("userInfoUrl");
    }
}
