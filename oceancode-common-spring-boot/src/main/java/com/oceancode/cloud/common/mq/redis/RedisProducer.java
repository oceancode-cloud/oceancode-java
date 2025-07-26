package com.oceancode.cloud.common.mq.redis;

import com.oceancode.cloud.api.mq.Callback;
import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.api.mq.RecordMeta;
import com.oceancode.cloud.common.util.RedisUtil;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;

import java.util.Collections;
import java.util.Objects;

public class RedisProducer implements Producer {
    @Override
    public void send(Message<?> message, Callback callback) {
        try {
            RecordMeta send = send(message);
            if (Objects.nonNull(send)) {
                callback.onSuccess(send.getData());
            }
        } catch (Throwable throwable) {
            callback.onException(throwable);
        }
    }

    @Override
    public RecordMeta send(Message<?> message) {
        String streamKey = message.getKey().replace("-", ":");
        RecordId recordId = RedisUtil.getDefaultTemplate()
                .opsForStream().add(StreamRecords.newRecord()
                        .ofMap(Collections.singletonMap("data", message))
                        .withStreamKey(streamKey));
        RecordMeta recordMeta = new RecordMeta();
        recordMeta.setData(recordId);
        return recordMeta;
    }
}
