package com.oceancode.cloud.api.ai;

import java.util.List;

public class AiMessage {
    private String id;
    private String model;
    private List<AiMessageContent> contents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<AiMessageContent> getContents() {
        return contents;
    }

    public void setContents(List<AiMessageContent> contents) {
        this.contents = contents;
    }
}

