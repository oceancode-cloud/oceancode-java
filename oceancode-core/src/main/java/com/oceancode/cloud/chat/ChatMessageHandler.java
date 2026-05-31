package com.oceancode.cloud.chat;

public interface ChatMessageHandler {
    void onMessage(ChatMessage message, ChatMessageCallback callback);
}
