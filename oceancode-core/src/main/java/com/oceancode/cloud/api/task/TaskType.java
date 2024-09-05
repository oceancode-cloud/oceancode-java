package com.oceancode.cloud.api.task;

import com.oceancode.cloud.api.TypeEnum;

public enum TaskType implements TypeEnum<String> {
    LOCAL("local", "local", null),
    POWER_JOB("powerjob", "powerjob", null),
    XXL_JOB("xxl_job", "xxl job", null),
    OTHER("other", "other", null),
    ;

    private String value;
    private String name;
    private String desc;

    TaskType(String value, String name, String desc) {
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
