package com.oceancode.cloud.common.cache.redis;

import com.oceancode.cloud.api.cache.DataCacheManager;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.common.cache.CommonCacheManager;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.RedisUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ConditionalOnBean(RedisCacheService.class)
@Component
public class RedisDataCacheManager extends CommonCacheManager implements DataCacheManager {
    private String source;
    private static final int MAX_MAP_ELEMENTS_COUNT = 30;

    @Resource
    private CommonConfig commonConfig;

    public RedisDataCacheManager(String source) {
        this.source = source;
    }

    protected RedisTemplate<String, Object> redisTemplate() {
        return RedisUtil.getTemplate(getSource());
    }


    @Override
    public void setString(String key, Object value) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);
        redisTemplate().opsForValue().set(express, value, getExpire(cacheId), TimeUnit.MILLISECONDS);
    }

    @Override
    public String getString(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);
        Object value = redisTemplate().opsForValue().get(express);
        if (Objects.isNull(value)) {
            return null;
        }
        if (Objects.equals(getEmptyValue(), value)) {
            return "";
        }
        if (value instanceof String str) {
            return str;
        }
        return value.toString();
    }

    @Override
    public void setObject(String key, Object value) {
        if (value instanceof Number || value instanceof String) {
            setString(key, value);
            return;
        }
        setString(key, value);
    }

    @Override
    public Map<String, Object> getObject(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);

        RedisTemplate<String, Object> redisTemplate = redisTemplate();
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(express, ScanOptions.scanOptions().match("*").count(MAX_MAP_ELEMENTS_COUNT + 1).build());
        Map<String, Object> resultMap = new HashMap<>();
        int count = 0;
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> item = cursor.next();
            resultMap.put(String.valueOf(item.getKey()), item.getValue());
            count++;
        }
        if (count > MAX_MAP_ELEMENTS_COUNT) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key:" + express + " elements count lager than " + MAX_MAP_ELEMENTS_COUNT);
        }
        if (resultMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return resultMap;
    }


    @Override
    public void setObjectList(String key, List<?> list) {
        setString(key, list);
    }

    @Override
    public <T> List<T> getObjectList(String key, Class<T> dataType) {
        String data = getString(key);
        if (Objects.isNull(data)) {
            return null;
        }
        if (ValueUtil.isEmpty(data)) {
            return List.of();
        }
        return JsonUtil.toList(key, dataType);
    }

    @Override
    public String getSource() {
        return "master";
    }

    @Override
    public void deleteByPrefix(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String pattern = express;
        if (!express.endsWith("*")) {
            pattern += "*";
        }
        String finalPattern = pattern;
        Set<String> keys = redisTemplate().execute((RedisCallback<Set<String>>) con -> {
            Set<String> keySet = new HashSet<>();
            try (Cursor<byte[]> cursor = con.scan(
                    ScanOptions.scanOptions().match(finalPattern).count(1000).build()
            )) {
                while (cursor.hasNext()) {
                    keySet.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return keySet;
        });

        if (keys != null && !keys.isEmpty()) {
            redisTemplate().delete(keys);
        }
    }

    @Override
    public void delete(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        redisTemplate().delete(express);
    }
}
