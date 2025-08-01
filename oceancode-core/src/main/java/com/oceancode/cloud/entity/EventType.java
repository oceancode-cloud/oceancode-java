package com.oceancode.cloud.entity;

import com.oceancode.cloud.api.TypeEnum;

public enum EventType implements TypeEnum<String> {
    ADD("add", "Add", null),
    DELETE("delete", "Delete", null),
    MODIFY("modify", "Modify", null),
    ;
    private String value;
    private String name;
    private String desc;

    EventType(String value, String name, String desc) {
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
