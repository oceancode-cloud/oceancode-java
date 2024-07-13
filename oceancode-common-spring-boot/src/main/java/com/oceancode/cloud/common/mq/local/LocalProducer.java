/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.common.mq.local;


import com.oceancode.cloud.api.mq.Callback;
import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.api.mq.RecordMeta;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.concurrent.CompletableFuture;

public class LocalProducer implements Producer {
    @Override
    public void send(Message<?> message, Callback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                GlobalLocalQueueManager.send(message);
                callback.onSuccess(null);
            } catch (InterruptedException e) {
                callback.onException(e);
            }
        });

    }

    @Override
    public RecordMeta send(Message<?> message) {
        try {
            GlobalLocalQueueManager.send(message);
            return null;
        } catch (InterruptedException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}
