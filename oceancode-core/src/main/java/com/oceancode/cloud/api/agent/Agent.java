package com.oceancode.cloud.api.agent;

import com.oceancode.cloud.chart.ChartMessage;
import com.oceancode.cloud.chat.ChatMessageChoice;
import com.oceancode.cloud.chat.ChatMessageInput;
import com.oceancode.cloud.chat.ChatMessageResponse;

public interface Agent {
    String getId();

    void chart(ChatMessageResponse response, ChartMessage chartMessage, ChatMessageInput messageInput);
}
