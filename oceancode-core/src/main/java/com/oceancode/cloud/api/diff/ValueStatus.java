package com.oceancode.cloud.api.diff;

import com.oceancode.cloud.api.TypeEnum;

public enum ValueStatus implements TypeEnum<Integer> {
    ADD(1, "Add", ""),
    UPDATE(3, "Update", ""),
    SET(4, "Set", ""),
    UNSET(5, "Unset", ""),
    ;

    private int value;
    private String name;
    private String desc;

    ValueStatus(int value, String name, String desc) {
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

    boolean isDeleted() {
        return UNSET.equals(this);
    }

    boolean isUpdated() {
        return SET.equals(this) || UPDATE.equals(this);
    }
}
