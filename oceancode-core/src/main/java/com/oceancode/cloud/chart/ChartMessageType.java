package com.oceancode.cloud.chart;

import com.oceancode.cloud.api.TypeEnum;

public enum ChartMessageType implements TypeEnum<Integer> {
    // 普通消息
    MESSAGE(1, null, null),

    // 通知消息
    NOTIFIER_MESSAGE(2, null, null),

    // 聊天消息
    CHART_MESSAGE(3, null, null),
    NOTIFIER_MESSAGE_PUSHED(4, null, null),
    ;
    private int value;
    private String name;
    private String desc;

    ChartMessageType(int value, String name, String desc) {
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
