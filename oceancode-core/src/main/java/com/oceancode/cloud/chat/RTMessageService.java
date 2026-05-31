package com.oceancode.cloud.chat;

import java.util.Set;

public interface RTMessageService {
    void sendToSelf(ChatMessage message);

    void sendTo(Long userId, ChatMessage message);

    void sendTo(ChatMessage message);

    void sendTo(Set<Long> userIds, ChatMessage message);

    boolean isOnline(Long userId);

    boolean isOnline(Set<Long> userIds);
}
