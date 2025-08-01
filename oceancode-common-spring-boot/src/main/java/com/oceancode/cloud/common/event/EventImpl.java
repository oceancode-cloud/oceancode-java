package com.oceancode.cloud.common.event;

import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.entity.EventNotifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventImpl {
    @Async
    public void sendAsync(EventNotifier event) {
        ComponentUtil.getApplicationContext().publishEvent(new CustomEvent(event));
    }

    public void send(EventNotifier event) {
        ComponentUtil.getApplicationContext().publishEvent(new CustomEvent(event));
    }
}
