package com.oceancode.cloud.chat;

import java.util.List;

public class ChatMessageInput {
    private String category;
    private Boolean group;
    private String id;
    private List<ChatMessageContent> contents;

    public List<ChatMessageContent> getContents() {
        return contents;
    }

    public void setContents(List<ChatMessageContent> contents) {
        this.contents = contents;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
