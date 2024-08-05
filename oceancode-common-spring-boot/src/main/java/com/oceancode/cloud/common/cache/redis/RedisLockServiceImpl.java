package com.oceancode.cloud.common.cache.redis;

import com.oceancode.cloud.api.LockActionCallback;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LockService;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Primary
@ConditionalOnClass({RedisTemplate.class,})
public class RedisLockServiceImpl implements LockService {

    @Resource
    private RedisCacheService redisCacheService;

    @Override
    public void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback) {
        List<String> keys = new ArrayList<>();
        keys.add(cacheKey.parseKey());
        final long threadId = Thread.currentThread().getId();

        List values = new ArrayList();
        values.add(threadId);
        values.add(timeout);
        Long result = redisCacheService.executeScript(cacheKey,
                "local key = KEYS[1] \n" +
                        "local threadId = ARGV[1] \n" +
                        "local time = ARGV[2] \n" +
                        "if (redis.call('exists',key) == 0) then\n" +
                        "    redis.call('hset',key,threadId,1);\n" +
                        "    redis.call('pexpire',key,time);\n" +
                        "    return 1;\n" +
                        "end;" +

                        "if (redis.call('hexists',key,threadId) == 1) then\n" +
                        "    redis.call('hincrby',key,threadId,'1');\n" +
                        "    redis.call('pexpire',key,time);\n" +
                        "    return 1;\n" +
                        "end;\n" +
                        "return 0;"
                , Long.class, keys, values);
        boolean ret = Objects.nonNull(result) && result.equals(1L);
        if (!ret) {
            return;
        }
        try {
            callback.doAction();
        } finally {
            result = redisCacheService.executeScript(cacheKey,
                    "local key = KEYS[1]\n" +
                            "local threadId = ARGV[1]\n" +
                            "local time = ARGV[2]\n" +

                            "if (redis.call('exists',key) == 0) then\n" +
                            "    return 0;\n" +
                            "end;\n" +
                            "local count = redis.call('hincrby',key,threadId,-1);\n" +
                            "if (count > 0) then\n" +
                            "    redis.call('pexpire',key,time);\n" +
                            "    return 1;\n" +
                            "else\n" +
                            "    redis.call('del',key);\n" +
                            "    return 1;\n" +
                            "end;"
                    , Long.class, keys, values);
            if (Objects.isNull(result) || result != 1) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "unlock failed,key:" + cacheKey.parseKey());
            }
        }
    }
}
