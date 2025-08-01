package com.oceancode.cloud.common.event;

import com.oceancode.cloud.entity.EventNotifier;
import org.springframework.context.ApplicationEvent;

public class CustomEvent extends ApplicationEvent {
    public CustomEvent(EventNotifier source) {
        super(source);
    }
}
