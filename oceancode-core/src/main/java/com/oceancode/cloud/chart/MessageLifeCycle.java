package com.oceancode.cloud.chart;

public enum MessageLifeCycle {
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
}
