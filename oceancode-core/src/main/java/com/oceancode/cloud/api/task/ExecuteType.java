package com.oceancode.cloud.api.task;

import com.oceancode.cloud.api.TypeEnum;

public enum ExecuteType implements TypeEnum<String> {
    AUTO("auto", "auto", null),
    MANUAL("manual", null, null),
    ;

    private String value;
    private String name;
    private String desc;

    ExecuteType(String value, String name, String desc) {
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
