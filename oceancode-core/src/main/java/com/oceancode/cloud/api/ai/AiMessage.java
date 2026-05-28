package com.oceancode.cloud.api.ai;

import java.util.List;

public class AiMessage {
    private String id;
    private String model;
    private String sessionId;
    private List<AiMessageContent> contents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

