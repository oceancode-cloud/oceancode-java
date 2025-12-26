package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.api.TypeEnum;

public enum AutoConfigType implements TypeEnum<String> {
    UPDATE("UPDATE"),
    ADD("ADD"),
    REMOVE("REMOVE"),

    ;
    private String type;

    AutoConfigType(String type) {
        this.type = type;
    }

    public static AutoConfigType from(String value) {
        return TypeEnum.from(value, AutoConfigType.class);
    }

    public static AutoConfigType from(String value, AutoConfigType defaultValue) {
        return TypeEnum.from(value, AutoConfigType.class, defaultValue);
    }

    public String getType() {
        return type;
    }

    @Override
    public String getValue() {
        return type;
    }

    @Override
    public String getName() {
        return type;
    }

    @Override
    public String getDescription() {
        return type;
    }
}
