package com.oceancode.cloud.chat;

import java.util.List;
import java.util.Map;

public class ChatMessageResponse {
    private String id;
    private String model;
    private Long created;
    private String sessionId;

    private List<ChatMessageChoice> choices;
    private Map<String, Object> extra;

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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public List<ChatMessageChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<ChatMessageChoice> choices) {
        this.choices = choices;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
