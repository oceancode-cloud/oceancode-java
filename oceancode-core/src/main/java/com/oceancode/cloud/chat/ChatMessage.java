package com.oceancode.cloud.chat;

import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.common.util.SessionUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatMessage {
    public static final String CHART_MESSAGE_KEY = "_chart-message";
    private ChatMessageType type;
    private Object data;
    private Long fromUser;
    private Long toUser;
    private String from;
    private String to;
    private String msgId;
    private ErrorCode errorCode;
    private Long projectId;
    private Long tenantId;
    private String category;
    private String dataId;
    private Map<String, Object> extra;
    private MessageLifeCycle lifeCycle = MessageLifeCycle.PROCESS;
    private String sessionId;
    private String uid;
    private UserType userType;
    private Long timestamp;

    private ChatMessage() {
    }

    public static ChatMessage notifier() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.NOTIFIER_MESSAGE);
        return chatMessage;
    }

    public static ChatMessage userMessage(Object data) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.CHAT_MESSAGE);
        chatMessage.setData(data);
        return chatMessage;
    }

    public ChatMessage replyChatMessage() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.CHAT_MESSAGE);
        chatMessage.setFromUser(this.getToUser());
        chatMessage.setToUser(SessionUtil.userId());
        chatMessage.setFrom(getTo());
        chatMessage.setMsgId(getMsgId());
        chatMessage.setTimestamp(System.currentTimeMillis());
        return chatMessage;
    }

    /**
     * 根据原始message复制 响应message
     *
     * @param source 原始message
     * @return response message
     */
    public static ChatMessage copy(ChatMessage source) {
        ChatMessage message = new ChatMessage();
        if (ChatMessageType.CHAT_MESSAGE.equals(source.getType())) {
            message.setData(source.getData());
            message.setMsgId(source.getMsgId());
            message.setFromUser(source.getFromUser());
            message.setType(source.getType());
            message.setToUser(source.getToUser());
        } else if (ChatMessageType.MESSAGE.equals(source.getType())) {
            message.setType(ChatMessageType.NOTIFIER_MESSAGE);
            message.setMsgId(source.getMsgId());
            message.setFromUser(source.getFromUser());
        }

        return message;
    }

    public static ChatMessage message() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.MESSAGE);
        return chatMessage;
    }

    public ChatMessageType getType() {
        return type;
    }

    public void setType(ChatMessageType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getFromUser() {
        return fromUser;
    }

    public void setFromUser(Long fromUser) {
        this.fromUser = fromUser;
    }

    public Long getToUser() {
        return toUser;
    }

    public void setToUser(Long toUser) {
        this.toUser = toUser;
    }

    public ChatMessage toUser(Long userId) {
        this.toUser = userId;
        return this;
    }

    public ChatMessage fromUser(Long userId) {
        this.fromUser = userId;
        return this;
    }

    public ChatMessage data(Object data) {
        this.data = data;
        return this;
    }

    public ChatMessage sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ChatMessage category(String category) {
        this.category = category;
        return this;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ChatMessage errorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.data = errorCode.getMessage();
        return this;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public ChatMessage msgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public ChatMessage dataId(String dataId) {
        this.dataId = dataId;
        return this;
    }

    public ChatMessage dataId(Long dataId) {
        return this.dataId(String.valueOf(dataId));
    }

    public ChatMessage lifeCycle(MessageLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
        return this;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public MessageLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(MessageLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public ChatMessage extraData(String key, String value) {
        setExtra(key, value);
        return this;
    }

    public ChatMessage extraData(String key, Number value) {
        setExtra(key, value);
        return this;
    }

    public Map<String, Object> getExtra() {
        if (Objects.isNull(extra)) {
            return Collections.emptyMap();
        }
        return extra;
    }

    private void setExtra(String key, Object value) {
        if (Objects.isNull(extra)) {
            extra = new HashMap<>();
        }
        extra.put(key, value);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChartMessage{" +
                "tenantId=" + tenantId +
                ", projectId=" + projectId +
                ", errorCode=" + errorCode +
                ", msgId='" + msgId + '\'' +
                ", toUser=" + toUser +
                ", fromUser=" + fromUser +
                ", type=" + type +
                '}';
    }
}
