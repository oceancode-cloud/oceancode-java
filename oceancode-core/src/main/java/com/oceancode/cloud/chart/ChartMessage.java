package com.oceancode.cloud.chart;

import com.oceancode.cloud.api.ErrorCode;

public class ChartMessage {
    public static final String CHART_MESSAGE_KEY = "_chart-message";
    private ChartMessageType type;
    private Object data;
    private Long fromUser;
    private Long toUser;
    private String msgId;
    private ErrorCode errorCode;
    private Long projectId;
    private Long tenantId;
    private String category;
    private String dataId;
    private String key;
    private MessageLifeCycle lifeCycle = MessageLifeCycle.PROCESS;

    private ChartMessage() {
    }

    public static ChartMessage notifier() {
        ChartMessage chartMessage = new ChartMessage();
        chartMessage.setType(ChartMessageType.NOTIFIER_MESSAGE);
        return chartMessage;
    }

    /**
     * 根据原始message复制 响应message
     *
     * @param source 原始message
     * @return response message
     */
    public static ChartMessage copy(ChartMessage source) {
        ChartMessage message = new ChartMessage();
        if (ChartMessageType.CHART_MESSAGE.equals(source.getType())) {
            message.setData(source.getData());
            message.setMsgId(source.getMsgId());
            message.setFromUser(source.getFromUser());
            message.setType(source.getType());
            message.setToUser(source.getToUser());
        } else if (ChartMessageType.MESSAGE.equals(source.getType())) {
            message.setType(ChartMessageType.NOTIFIER_MESSAGE);
            message.setMsgId(source.getMsgId());
            message.setFromUser(source.getFromUser());
        }

        return message;
    }

    public static ChartMessage message() {
        ChartMessage chartMessage = new ChartMessage();
        chartMessage.setType(ChartMessageType.MESSAGE);
        return chartMessage;
    }

    public ChartMessageType getType() {
        return type;
    }

    public void setType(ChartMessageType type) {
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

    public ChartMessage toUser(Long userId) {
        this.toUser = userId;
        return this;
    }

    public ChartMessage fromUser(Long userId) {
        this.fromUser = userId;
        return this;
    }

    public ChartMessage data(Object data) {
        this.data = data;
        return this;
    }

    public ChartMessage category(String category) {
        this.category = category;
        return this;
    }

    public ChartMessage key(String key) {
        this.key = key;
        return this;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ChartMessage errorCode(ErrorCode errorCode) {
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

    public ChartMessage msgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public ChartMessage dataId(String dataId) {
        this.dataId = dataId;
        return this;
    }

    public ChartMessage dataId(Long dataId) {
        return this.dataId(String.valueOf(dataId));
    }

    public ChartMessage lifeCycle(MessageLifeCycle lifeCycle) {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MessageLifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(MessageLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
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
