package com.oceancode.cloud.api.agent;

import com.oceancode.cloud.chat.ChatMessage;
import com.oceancode.cloud.chat.ChatMessageInput;
import com.oceancode.cloud.chat.ChatMessageResponse;

public interface Agent {
    String getId();

    void chat(ChatMessageResponse response, ChatMessage chatMessage, ChatMessageInput messageInput);
}
