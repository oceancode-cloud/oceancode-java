package com.oceancode.cloud.common.event;

import com.oceancode.cloud.api.event.EventParam;
import com.oceancode.cloud.common.util.ComponentUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EventImpl {
    @Async
    public void sendAsync(EventParam event) {
        ComponentUtil.getApplicationContext().publishEvent(new CustomEvent(event));
    }

    public void send(EventParam event) {
        ComponentUtil.getApplicationContext().publishEvent(new CustomEvent(event));
    }
}
