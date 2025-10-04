package com.oceancode.cloud.api.event;

public interface EventStep {
    void setData(EventParam param);

    EventParam getData();

}
