/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.springboot.simple.demo.core.common.enums;

import com.oceancode.cloud.api.TypeEnum;

public enum EnvType implements TypeEnum<String> {
    DEV("dev"),
    TEST("test"),
    PROD("prod"),
    ;
    private String type;

    EnvType(String type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return type;
    }

    @Override
    public String getName() {
        return type;
    }

    @Override
    public String getDescription() {
        return type;
    }
}
