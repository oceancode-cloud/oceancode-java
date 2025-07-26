package com.oceancode.cloud.common.id;

import com.oceancode.cloud.api.IncrementIdGenerator;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.common.cache.KeyParam;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class RedisIncrementIdGenerator implements IncrementIdGenerator {

    @Resource
    private RedisCacheService redisCacheService;

    @Override
    public Long nextId(String namespace, Supplier<Long> baseFunc, int setup) {
        Long ret = process(namespace, null, setup, false);
        if (ret == -2L) {
            return process(namespace, baseFunc.get(), setup, true);
        }
        return ret;
    }

    @Override
    public Long nextId(String namespace, Long base, int setup) {
        return process(namespace, base, setup, true);
    }

    public Long process(String namespace, Long base, int setup, boolean hasBaseValue) {
        CacheKey key = KeyParam.of().express("_id:incr:" + namespace);
        if (Objects.isNull(base)) {
            base = 1L;
        }

        String script = """
                local key = KEYS[1]
                local time = ARGV[2]
                local setup = tonumber(ARGV[4])
                                
                if(redis.call('exists',key)==1) then
                    local id = redis.call('get', key) + steup;
                    redis.call('set', key, id);
                    redis.call('expire', key, time);
                    return id;
                end;
                                
                local hasBaseValue = ARGV[3]
                if(redis.call('exists', key) == 0) then
                    if(ARGV[3] == "false") then
                        return -2;
                    end;
                    local base = ARGV[1];
                    redis.call('set', key, base);
                    redis.call('expire',key,time);
                    return base;
                end;""";

        List<String> keys = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        keys.add(key.parseKey());

        values.add(base);
        values.add(24 * 60 * 60 * 1000);
        values.add(hasBaseValue);
        values.add(setup);

        Long result = redisCacheService.executeScript(key, script, Long.class, keys, values);
        return result;

    }

}
