package com.oceancode.cloud.common.mq.redis;


import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.api.mq.MessageUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.RedisUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        } finally {
            SessionUtil.remove();
        }
    }

    private void processMessage(ObjectRecord<String, String> message) {
        Message<Object> msg = JsonUtil.toBean(message.getValue(), Message.class);
        MessageUtil.setSession(msg);
        Object value = msg.getData();
        msg.setData(null);
        Class<?> dataType = consumer.getDataType(msg);
        if (Objects.nonNull(dataType) && Objects.nonNull(value)) {
            if (value instanceof List list) {
                if (!list.isEmpty() && list.get(0) instanceof Map<?, ?>) {
                    List<Map<String, Object>> mapList = (List<Map<String, Object>>) value;
                    value = JsonUtil.mapToBean(mapList, dataType);
                }
            } else if (value instanceof Map map) {
                value = JsonUtil.mapToBean(map, dataType);
            }
        }
        msg.setData(value);

        RedisTemplate<String, Object> defaultTemplate = RedisUtil.getDefaultTemplate();
        try {
            consumer.poll(Arrays.asList(msg));
        } catch (Throwable throwable) {
            LOGGER.error("redis consumer failed.sourceKey:{},msgId:{},msgType:", msg.getSourceKey(), msg.getId(), msg.getMessageType(), throwable);
            throw throwable;
        }

        defaultTemplate
                .opsForStream().acknowledge(msg.getKey(), RedisStreamConfig.userEventGroup, message.getId());
        defaultTemplate
                .opsForStream().delete(msg.getKey(), message.getId());

    }
}
