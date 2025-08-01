package com.oceancode.cloud.api.notifier;


import com.oceancode.cloud.common.entity.Domain;
import com.oceancode.cloud.entity.EventNotifier;

public interface DomainNotifier extends Notifier {

    @Override
    default boolean support(EventNotifier eventNotifier) {
        if (eventNotifier.hasClassType(Domain.class)) {
            return true;
        }
        if (eventNotifier.getNewValue() instanceof Domain || eventNotifier.getOldValue() instanceof Domain) {
            return true;
        }
        return false;
    }
}
