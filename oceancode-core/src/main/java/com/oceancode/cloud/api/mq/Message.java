package com.oceancode.cloud.api.mq;

public class Message<T> {
    private String id;
    private String key;

    private T data;

    private Long userId;

    private Long projectId;

    private Long tenantId;

    private String username;

    private String group;

    private transient MessageType messageType = MessageType.MESSAGE;

    private String traceId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", userId=" + userId +
                ", projectId=" + projectId +
                ", tenantId=" + tenantId +
                ", group='" + group + '\'' +
                ", messageType=" + messageType +
                ", traceId='" + traceId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
