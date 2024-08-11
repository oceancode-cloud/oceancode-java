package com.oceancode.cloud.api.fetcher;

import com.oceancode.cloud.api.TypeEnum;

public enum FetcherType implements TypeEnum<Integer> {
    API(1, "api", null),
    ;
    private int value;
    private String name;
    private String desc;

    FetcherType(int value, String name, String desc) {
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
}
