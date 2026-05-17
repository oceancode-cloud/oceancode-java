package com.oceancode.cloud.common.mq.redis;

import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.chart.ChartMessage;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.RedisUtil;
import com.oceancode.cloud.common.util.VersionUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

@Configuration
@ConditionalOnClass(StreamListener.class)
public class RedisStreamConfig implements InitializingBean, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisStreamConfig.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public static String streamName;
    public static String userEventGroup;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired(required = false)
    private com.oceancode.cloud.api.mq.Consumer consumer;

    public RedisStreamConfig(CommonConfig commonConfig, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        redisTemplate = RedisUtil.getTemplate(KeyParam.of().pattern());
        streamName = commonConfig.getValue("oc.message.queue." + KeyParam.DEFAULT_KEY + ".name", ChartMessage.CHART_MESSAGE_KEY.replace("-", ":"));
        userEventGroup = commonConfig.getValue("oc.message.queue." + KeyParam.DEFAULT_KEY + ".group", "user-event-stream");
    }

    @Bean
//    @ConditionalOnMissingBean(Producer.class)
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnBean(com.oceancode.cloud.api.mq.Consumer.class)
    public RedisConsumer redisConsumer() {
        return new RedisConsumer();
    }


    @Bean
    @ConditionalOnBean({com.oceancode.cloud.api.mq.Consumer.class, RedisConsumer.class})
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> messageListenerContainer(RedisConnectionFactory connectionFactory, RedisConsumer redisConsumer) throws Exception {
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> listenerContainer = streamContainer(streamName, connectionFactory, redisConsumer);
        listenerContainer.start();
        return listenerContainer;
    }

    private StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamContainer(String streamName, RedisConnectionFactory connectionFactory, StreamListener<String, ObjectRecord<String, String>> streamListener) throws Exception {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(5)) // 拉取消息超时时间
                        .batchSize(10) // 批量抓取消息
                        .targetType(String.class) // 传递的数据类型
                        .executor(threadPoolTaskExecutor)
                        .build();
        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container = StreamMessageListenerContainer
                .create(connectionFactory, options);
        // 指定消费最新的消息
        StreamOffset<String> offset = StreamOffset.create(streamName, ReadOffset.lastConsumed());
        // 创建消费者
        StreamMessageListenerContainer.StreamReadRequest<String> streamReadRequest = buildStreamReadRequest(offset, streamListener);
        // 指定消费者对象
        container.register(streamReadRequest, streamListener);
        return container;
    }

    private StreamMessageListenerContainer.StreamReadRequest<String> buildStreamReadRequest(StreamOffset<String> offset, StreamListener<String, ObjectRecord<String, String>> streamListener) throws Exception {
        Consumer consumer;
        if (streamListener instanceof RedisConsumer) {
            consumer = Consumer.from(userEventGroup, InetAddress.getLocalHost().getHostName());
        } else {
            throw new Exception("无法识别的 stream key");
        }
        // 关闭自动 ack 确认
        return StreamMessageListenerContainer.StreamReadRequest.builder(offset)
                .errorHandler((error) -> LOGGER.error("redis-message-queue error", error))
                .cancelOnError(e -> false)
                .consumer(consumer)
                // 关闭自动 ack 确认
                .autoAcknowledge(false)
                .build();
    }

    /**
     * 检查 Redis 版本是否符合要求
     *
     * @throws IllegalStateException 如果 Redis 版本小于 5.0.0 版本，抛出该异常
     */
    private void checkRedisVersion() {
        // 获得 Redis 版本
        Properties info = redisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        Assert.notNull(info, "Redis info is null");
        Object redisVersion = info.get("redis_version");
        boolean isValid = false;
        if (redisVersion instanceof String version) {
            if (VersionUtil.compareVersion(version, "5") >= 0) {
                isValid = true;
            }
        }
        if (!isValid) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "redis version must not less 5,expect: >=5,actual" + redisVersion);
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.isNull(consumer)) {
            return;
        }
        checkRedisVersion();
        StreamOperations<String, Object, Object> streamOperations = redisTemplate.opsForStream();
        try {
            streamOperations.createGroup(streamName, ReadOffset.from("0"), userEventGroup);
        } catch (Exception e) {
            LOGGER.error("create group error.", e);
        }
    }

}
