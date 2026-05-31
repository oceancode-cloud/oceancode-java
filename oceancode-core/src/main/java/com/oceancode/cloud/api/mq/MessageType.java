/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.api.mq;

import com.oceancode.cloud.api.TypeEnum;

public enum MessageType implements TypeEnum<String> {
    LOG("log", "Log", null),
    MESSAGE("message", "Message", null),
    CHAT_MESSAGE("chart_message", "Chart Message", null),
    DELAY_MESSAGE("delay_message", "Delay Message", null),

    ;
    private String value;
    private String name;
    private String desc;

    MessageType(String value, String name, String desc) {
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
