package com.oceancode.cloud.api.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserBaseInfo {
    private Long userId;
    private String openid;
    private Map<String, Object> param;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public UserBaseInfo addParam(String key, Object value) {
        if (Objects.isNull(this.param)) {
            this.param = new HashMap<>();
        }
        this.param.put(key, value);
        return this;
    }

    public UserBaseInfo username(String username) {
        return addParam("username", username);
    }

    public UserBaseInfo nickname(String nickname) {
        return addParam("nickname", nickname);
    }

    public UserBaseInfo avatar(String avatar) {
        return addParam("avatar", avatar);
    }

    public String username() {
        return (String) getValue("username");
    }

    public String nickname() {
        return (String) getValue("nickname");
    }

    public String avatar() {
        return (String) getValue("avatar");
    }

    private Object getValue(String key) {
        return param.get(key);
    }
}
