package com.oceancode.cloud.api.task;

import com.oceancode.cloud.api.TypeEnum;

public enum ExpressType implements TypeEnum<String> {
    CORN("cron", null, null),
    FIXED("fixed", null, null),
    INTERVAL("interval", null, null),
    TRIGGER("trigger", null, null),
    ;

    private String value;
    private String name;
    private String desc;

    ExpressType(String value, String name, String desc) {
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
