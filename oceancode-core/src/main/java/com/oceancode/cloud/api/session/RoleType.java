package com.oceancode.cloud.api.session;

import com.oceancode.cloud.api.TypeEnum;

public enum RoleType implements TypeEnum<String> {
    VISITOR("visitor", "参与者", ""),
    CREATOR("creator", "创建者", ""),
    DEVELOPER("developer", "开发者", ""),
    COMMITER("commiter", "Commiter", ""),
    ADMIN("admin", "管理员", ""),
    GUEST("guest", "游客", ""),
    SUPER_ADMIN("super_admin", "超级管理员", ""),
    CUSTOM("custom", "自定义", null);
    private String value;
    private String name;
    private String description;

    public static RoleType from(String role) {
        return TypeEnum.from(role, RoleType.class);
    }

    RoleType(String value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
