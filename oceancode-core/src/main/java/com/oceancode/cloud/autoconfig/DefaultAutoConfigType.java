package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.TypeEnum;

public enum DefaultAutoConfigType implements AutoConfigType {
    ADD(1, "新增", "新增一条记录"),
    ADD_MANY(2, "批量新增", "新增多条记录"),
    UPDATE(3, "修改", "修改一条记录"),
    UPDATE_MANY(4, "批量修改", "批量修改记录"),
    REMOVE(5, "删除", "删除一条记录"),
    REMOVE_MANY(6, "批量删除", "删除多条记录"),
    FMOVE(7, "移动", "在同一分组下移动"),
    NMOVE(8, "移动", "在不同分组下移动"),
    CUSTOM(-1, "自定义", "自定义类型");

    private int type;
    private String name;
    private String description;

    DefaultAutoConfigType(int type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public static AutoConfigType from(Integer type) {
        return TypeEnum.from(type, DefaultAutoConfigType.class);
    }

    @Override
    public Integer getValue() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
