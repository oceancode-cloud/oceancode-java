package com.oceancode.cloud.api.session;

import com.oceancode.cloud.api.TypeEnum;

public enum UserType implements TypeEnum<Integer> {
    EXAMPLE(0, "example user", null),
    USER(1, "primary user", null),
    GEUST(2, "", null),
    ;

    private int value;
    private String name;
    private String desc;

    UserType(int value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
