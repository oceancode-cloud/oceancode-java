package com.oceancode.cloud.common.event;

import com.oceancode.cloud.api.event.EventParam;
import org.springframework.context.ApplicationEvent;

public class CustomEvent extends ApplicationEvent {
    public CustomEvent(EventParam source) {
        super(source);
    }
}
