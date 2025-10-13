package com.oceancode.cloud.chart;

import com.oceancode.cloud.api.TypeEnum;

public enum MessageLifeCycle implements TypeEnum<Integer> {
    START(0),
    PROCESS(1),
    FINISH(1),
    ;

    private int type;

    MessageLifeCycle(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public Integer getValue() {
        return type;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
