package com.oceancode.cloud.common.event;

import com.oceancode.cloud.api.event.EventHandler;
import com.oceancode.cloud.api.event.EventParam;
import com.oceancode.cloud.api.notifier.Notifier;
import com.oceancode.cloud.entity.EventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


@Component
public class EventListener implements ApplicationListener<CustomEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    private List<EventHandler> notifiers;

    public EventListener(List<EventHandler> notifiers) {
        this.notifiers = notifiers;
    }

    @Override
    public void onApplicationEvent(CustomEvent event) {
        if (!(event.getSource() instanceof EventParam eventParam)) {
            return;
        }
        notifiers.stream().filter(notifier -> {
            try {
                return notifier.support(eventParam);
            } catch (Throwable throwable) {
                LOGGER.error("error.", throwable);
                return false;
            }
        }).forEach(notifier -> {
            try {
                notifier.handler(eventParam);
            } catch (Throwable throwable) {
                LOGGER.error("error.", throwable);
            }
        });
    }
}
