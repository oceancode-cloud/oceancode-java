package com.oceancode.cloud.chart;

import java.util.Set;

public interface Messenger {
    void sendToSelf(ChartMessage message);

    void sendTo(Long userId, ChartMessage message);

    void sendTo(Set<Long> userIds, ChartMessage message);

    boolean isOnline(Long userId);

    boolean isOnline(Set<Long> userIds);
}
