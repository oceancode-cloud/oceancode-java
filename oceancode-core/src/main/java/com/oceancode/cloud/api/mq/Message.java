package com.oceancode.cloud.api.mq;

public class Message<T> {
    private String id;
    private String key;

    private T data;

    private Long userId;

    private Long projectId;

    private Long tenantId;

    private String requestId;

    private String cursor;

    private String username;

    private String group;

    /**
     * 业务自定义，可用于消费者做业务上的区分
     */
    private String sourceKey;

    private transient MessageType messageType = MessageType.MESSAGE;

    private String traceId;

    public static <T> Message<T> log(String key) {
        Message<T> message = new Message<>();
        message.setMessageType(MessageType.LOG);
        message.setKey(key);
        return message;
    }

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

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
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
