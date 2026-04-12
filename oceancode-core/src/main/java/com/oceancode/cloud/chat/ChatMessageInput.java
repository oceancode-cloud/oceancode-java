package com.oceancode.cloud.chat;

import java.util.List;

public class ChatMessageInput {
    private List<ChatMessageContent> contents;

    public List<ChatMessageContent> getContents() {
        return contents;
    }

    public void setContents(List<ChatMessageContent> contents) {
        this.contents = contents;
    }
}
