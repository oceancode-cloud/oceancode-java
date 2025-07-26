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

    @Override
    public String toString() {
        return "ChartMessage{" +
                "errorCode=" + errorCode +
                ", msgId='" + msgId + '\'' +
                ", toUser=" + toUser +
                ", fromUser=" + fromUser +
                ", type=" + type +
                '}';
    }
}
