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
}
