package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.event.EventImpl;
import com.oceancode.cloud.entity.EventNotifier;

public class EventUtil {
    private EventUtil() {
    }

    public static void send(EventNotifier event) {
        send(event, true);
    }

    public static void send(EventNotifier event, boolean isAsync) {
        if (isAsync) {
            ComponentUtil.getBean(EventImpl.class).sendAsync(event);
        } else {
            ComponentUtil.getBean(EventImpl.class).send(event);
        }
    }
}
