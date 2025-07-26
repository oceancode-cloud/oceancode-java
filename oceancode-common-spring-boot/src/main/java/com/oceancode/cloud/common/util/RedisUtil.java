package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.cache.KeyParam;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public final class RedisUtil {
    private RedisUtil() {
    }


    public static RedisTemplate<String, Object> builderRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(JsonUtil.getObjectMapper(), Object.class);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public static RedisTemplate<String, Object> getTemplate(String sourceKey) {
        return ComponentUtil.getBean(sourceKey + "RedisTemplate", RedisTemplate.class);
    }

    public static RedisTemplate<String, Object> getDefaultTemplate() {
        return getTemplate(KeyParam.DEFAULT_KEY);
    }

}
