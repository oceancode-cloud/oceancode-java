package com.oceancode.cloud.chat;

public class ChatMessageChoice {
    private String role;
    private ChatMessageResponseContent message;
    private Integer index;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ChatMessageResponseContent getMessage() {
        return message;
    }

    public void setMessage(ChatMessageResponseContent message) {
        this.message = message;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
