package com.oceancode.cloud.chart;

import com.oceancode.cloud.api.TypeEnum;

public enum UserType implements TypeEnum<String> {
    ASSISTANT("5", "Assistant", "Ai Assistant");

    private String value;
    private String name;
    private String desc;

    UserType(String value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
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
        return desc;
    }
}
