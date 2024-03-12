/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.cache;

import com.oceancode.cloud.api.TypeEnum;

public enum CacheType implements TypeEnum<String> {
    REDIS("redis", null, null),
    LOCAL("local", null, null),
    ;

    private String value;

    private String name;
    private String desc;

    CacheType(String value, String name, String desc) {
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
