package com.oceancode.cloud.chat;

import com.oceancode.cloud.api.ai.AiResponse;

public interface ChatService {
    String getType();

    AiResponse chat(ChatMessageInput message, ChatCallback callback);
}
