package com.oceancode.cloud.entity;

import com.oceancode.cloud.api.TypeEnum;

public enum EventTag implements TypeEnum<String> {
    CACHE("cache", "Cache", null),
    ;
    private String value;
    private String name;
    private String desc;


    EventTag(String value, String name, String desc) {
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
