/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.enums;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.config.Config;

public enum AppModeType implements TypeEnum<Integer> {
    STANDALONE(0, "app", "startup with standalone"),
    MICROSERVICE(Config.App.MICROSERVICE, "service", "startup with microservice"),

    ;


    private int value;
    private String name;
    private String desc;

    AppModeType(int value, String name, String desc) {
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
