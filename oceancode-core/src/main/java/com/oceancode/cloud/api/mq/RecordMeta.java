/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.api.mq;

import java.util.Objects;

public class RecordMeta {
    private String messageId;
    private Long offsetMsgId;

    private Object data;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getOffsetMsgId() {
        return offsetMsgId;
    }

    public void setOffsetMsgId(Long offsetMsgId) {
        this.offsetMsgId = offsetMsgId;
    }
}
