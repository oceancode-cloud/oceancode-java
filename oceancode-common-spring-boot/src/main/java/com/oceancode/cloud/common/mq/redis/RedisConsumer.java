package com.oceancode.cloud.common.mq.redis;


import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.RedisUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.util.Arrays;
import java.util.Objects;

public class RedisConsumer implements StreamListener<String, ObjectRecord<String, String>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisConsumer.class);
    @Resource
    private Consumer consumer;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        try {
            processMessage(message);
        } catch (Throwable e) {
            LOGGER.error("consumer message error.key:{},msgId:{}", consumer.getKey(), message.getId().toString(), e);
        }
    }

    private void processMessage(ObjectRecord<String, String> message) {
        Message<Object> msg = new Message<>();
        msg.setKey(message.getStream());
        msg.setId(message.getId().toString());
        msg.setData(message.getValue());

        Class<?> dataType = consumer.getDataType();
        if (Objects.nonNull(dataType)) {
            Object value = message.getValue();
            if (value instanceof String str) {
                if (str.trim().startsWith("[")) {
                    value = JsonUtil.toList(str, Message.class, dataType);
                } else {
                    value = JsonUtil.toBean(str, Message.class, dataType);
                }
            }
            msg.setData(value);
        }

        consumer.poll(Arrays.asList(msg));

        RedisUtil.getDefaultTemplate()
                .opsForStream().acknowledge(msg.getKey(), RedisStreamConfig.userEventGroup, message.getId());
        RedisUtil.getDefaultTemplate()
                .opsForStream().delete(msg.getKey(), message.getId());

    }
}
