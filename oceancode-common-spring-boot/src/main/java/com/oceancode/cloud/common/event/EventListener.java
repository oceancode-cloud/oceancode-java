package com.oceancode.cloud.common.event;

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

    private List<Notifier> notifiers;

    public EventListener(List<Notifier> notifiers) {
        this.notifiers = notifiers;
    }

    @Override
    public void onApplicationEvent(CustomEvent event) {
        if (Objects.isNull(event) || !(event.getSource() instanceof EventNotifier)) {
            return;
        }
        EventNotifier eventNotifier = (EventNotifier) event.getSource();
        notifiers.stream().filter(notifier -> {
            try {
                return notifier.support(eventNotifier);
            } catch (Throwable throwable) {
                LOGGER.error("error.", throwable);
                return false;
            }
        }).forEach(notifier -> {
            try {
                notifier.notifier(eventNotifier);
            } catch (Throwable throwable) {
                LOGGER.error("error.", throwable);
            }
        });
    }
}
