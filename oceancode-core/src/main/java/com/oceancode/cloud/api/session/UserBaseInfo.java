package com.oceancode.cloud.api.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserBaseInfo {
    private Long userId;
    private String openid;
    private UserType userType;
    private Map<String, Object> data;
    private String role;
    private transient RoleType roleType;

    public RoleType role() {
        if (Objects.nonNull(roleType)) {
            return roleType;
        }
        roleType = RoleType.from(role);
        return roleType;
    }

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public UserBaseInfo addParam(String key, Object value) {
        if (Objects.isNull(this.data)) {
            this.data = new HashMap<>();
        }
        if ("username".equals(key)) {
            if (value instanceof char[]) {
                value = String.valueOf(value);
            }
        }
        this.data.put(key, value);
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
        if (Objects.isNull(data)) {
            return null;
        }
        return data.get(key);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
