package com.oceancode.cloud.api.notifier;

import com.oceancode.cloud.entity.EventNotifier;

public interface Notifier {

    boolean support(EventNotifier eventNotifier);

    void notifier(EventNotifier eventNotifier);
}
