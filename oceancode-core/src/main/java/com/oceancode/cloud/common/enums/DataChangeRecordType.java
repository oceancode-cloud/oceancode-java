package com.oceancode.cloud.common.enums;

import com.oceancode.cloud.api.TypeEnum;

public enum DataChangeRecordType implements TypeEnum<Integer> {
    RAW(0, "raw", null),
    ADD(1, "add", ""),
    DELETE(2, "delete", ""),
    UPDATE(3, "update", null),
    ;

    DataChangeRecordType(int value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    private int value;
    private String name;
    private String desc;

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
