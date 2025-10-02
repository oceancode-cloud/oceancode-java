package com.oceancode.cloud.api.event;

public interface EventHandler<T extends EventParam> {
    boolean support(EventParam param);

    void handler(T param);
}
