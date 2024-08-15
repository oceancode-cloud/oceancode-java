/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.cache.redis;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.cache.entity.SortedValue;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.CacheUtil;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Primary
@ConditionalOnClass({RedisTemplate.class,})
public class RedisCacheServiceImpl implements RedisCacheService {
    private static final int MAX_MAP_ELEMENTS_COUNT = 30;

    private static RedisTemplate<String, Object> redisTemplate(String sourceKey) {
        return ComponentUtil.getBean(sourceKey + "RedisTemplate", RedisTemplate.class);
    }

    @Override
    public void setString(CacheKey keyParam, Object value) {
        long expire;
        if (value == null) {
            boolean enabledEmpty = CacheUtil.emptyEnabled(keyParam.key());
            if (!enabledEmpty) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "value must not null");
            }
            value = CacheUtil.emptyValue(keyParam.key());
            expire = CacheUtil.emptyExpire(keyParam.key());
        } else {
            expire = keyParam.expire();
        }
        String val = JsonUtil.toJson(value);
        checkKey(keyParam, val);
        int maxReplica = CacheUtil.replica(keyParam.key());
        if (maxReplica > 1) {
            int maxLength = CacheUtil.maxLength(keyParam.key());
            int length = val.length();
            int replica = length / maxLength + (length % maxLength == 0 ? 0 : 1);
            if (replica > maxReplica) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "the replica number of key:(" + keyParam.parseKey() + ") is more than " + maxReplica + ",current:" + replica);
            }

            Map<String, Object> values = new HashMap<>();
            int startIndex = 0;
            values.put("count", replica);
            for (int index = 0; index < replica; index++) {
                int endIndex = startIndex + maxLength;
                if (endIndex > length) {
                    endIndex = length;
                }
                String temp = val.substring(startIndex, endIndex);
                values.put("_" + index, temp);
                startIndex = endIndex;
            }
            setMap(keyParam, values);
            return;
        }

        redisTemplate(keyParam.sourceKey()).opsForValue().set(keyParam.parseKey(), value, expire, TimeUnit.MILLISECONDS);
        if (CacheUtil.enabledAb(keyParam.key())) {
            redisTemplate(keyParam.sourceKey()).opsForValue().set(keyParam.parseBKey(), value, expire + 1000, TimeUnit.MILLISECONDS);
        }
    }


    private void checkKey(CacheKey keyParam, String value) {
        int length = value.length();
        if (length * 2 > CacheUtil.totalLength(keyParam.key())) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "the maximum value length of key:(" + keyParam.parseKey() + ") is more than " + CacheUtil.totalLength(keyParam.key()) + ",current:" + length * 2);
        }
    }


    @Override
    public String getString(CacheKey keyParam) {
        int maxReplica = CacheUtil.replica(keyParam.key());
        if (maxReplica > 1) {
            Map<String, Object> values = getMap(keyParam);
            if (ValueUtil.isEmpty(values)) {
                return null;
            }
            Integer count = (Integer) values.get("count");
            if (Objects.isNull(count) || count <= 0) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < count; index++) {
                sb.append(values.get("_" + index));
            }
            String text = sb.toString();
            if (CacheUtil.enabledAb(keyParam.key())) {
                if (CacheUtil.isEmpty(keyParam.key(), text)) {
                    return null;
                }
            }
            return text;
        }
        Object value = redisTemplate(keyParam.sourceKey()).opsForValue().get(keyParam.parseKey());
        if (Objects.isNull(value)) {
            if (CacheUtil.enabledAb(keyParam.key())) {
                value = redisTemplate(keyParam.sourceKey()).opsForValue().get(keyParam.parseBKey());
            }
        }
        if (Objects.isNull(value)) {
            return null;
        }
        if (CacheUtil.isEmpty(keyParam.key(), value)) {
            boolean enabledEmpty = CacheUtil.emptyEnabled(keyParam.key());
            if (enabledEmpty) {
                return (String) value;
            }
        }
        if (value instanceof List || value instanceof Map) {
            return JsonUtil.toJson(value);
        }
        return String.valueOf(value);
    }

    @Override
    public <T> List<T> getStringAsList(CacheKey keyParam, Class<T> returnClassType) {
        String value = getString(keyParam);
        if (null == value) {
            return null;
        }
        if (CacheUtil.isEmpty(keyParam.key(), value)) {
            return Collections.emptyList();
        }
        return JsonUtil.toList(value, returnClassType);
    }

    @Override
    public void setStringAsList(CacheKey key, List<?> list) {
        setString(key, list);
    }

    @Override
    public void setMap(CacheKey keyParam, Map<String, Object> value) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "value is required");
        }
        if (value.size() > MAX_MAP_ELEMENTS_COUNT) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "value elements is lager(max:" + MAX_MAP_ELEMENTS_COUNT + "),current:" + value.size());
        }
        String key = keyParam.parseKey();
        List<String> keys = new ArrayList<>();
        List values = new ArrayList();
        keys.add(key);
        values.add(keyParam.expire());
        String keyId = keyParam.key();
        boolean enabledEmptyVal = CacheUtil.emptyEnabled(keyId);
        String emptyValue = CacheUtil.emptyValue(keyId);
        for (Map.Entry<String, Object> item : value.entrySet()) {
            keys.add(item.getKey());
            Object val = item.getValue();
            if (val == null && enabledEmptyVal) {
                val = emptyValue;
            }
            if (val instanceof String) {
                if (Objects.nonNull(val)) {
                    checkKey(keyParam, (String) val);
                }
            }
            values.add(val);
        }

        if (value.size() > CacheUtil.maxLength(keyId)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "the maximum size of key:(" + keyParam.parseKey() + ") is more than " + CacheUtil.maxLength(keyId) + ",current:" + value.size());
        }

        Long result = executeScript(keyParam, "if (redis.call('EXISTS', KEYS[1]) == 0) then\n" + "    redis.call('HSET', KEYS[1], KEYS[2], ARGV[2]);\n" + "end\n" + "redis.call('PEXPIRE', KEYS[1], ARGV[1]);\n" + "if (redis.call('EXISTS', KEYS[1]) == 0) then\n" + "    return -1;\n" + "end\n" + "for i, v in pairs(KEYS) do\n" + "    if (i > 1) then\n" + "        redis.call('HSET', KEYS[1], v, ARGV[i]);\n" + "    end\n" + "end\n" + "return 1;", Long.class, keys, values);
        if (result == null || result != 1) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "setMap keyId:%s key:%s return:%d error", keyId, key, result);
        }
    }

    @Override
    public Map<String, Object> getMap(CacheKey keyParam) {
        RedisTemplate<String, Object> redisTemplate = redisTemplate(keyParam.sourceKey());
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(keyParam.parseKey(), ScanOptions.scanOptions().match("*").count(MAX_MAP_ELEMENTS_COUNT + 1).build());
        Map<String, Object> resultMap = new HashMap<>();
        int count = 0;
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> item = cursor.next();
            resultMap.put(String.valueOf(item.getKey()), item.getValue());
            count++;
        }
        if (count > MAX_MAP_ELEMENTS_COUNT) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key:" + keyParam.parseKey() + " elements count lager than " + MAX_MAP_ELEMENTS_COUNT);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getMapValues(CacheKey keyParam, List<String> fields) {
        RedisTemplate<String, Object> redisTemplate = redisTemplate(keyParam.sourceKey());
        List values = redisTemplate.opsForHash().multiGet(keyParam.parseKey(), (List) fields);
        Map<String, Object> resultMap = new HashMap<>();
        for (int index = 0; index < fields.size(); index++) {
            String key = fields.get(index);
            Object value = values.get(index);
            if (CacheUtil.isEmpty(keyParam.key(), value)) {
                value = null;
            }
            resultMap.put(key, value);
        }

        return resultMap;
    }

    @Override
    public void setMapValue(CacheKey keyParam, String key, Object value) {
        RedisTemplate<String, Object> redisTemplate = redisTemplate(keyParam.sourceKey());
        redisTemplate.opsForHash().put(keyParam.parseKey(), key, value);
    }

    @Override
    public void setMapValues(CacheKey keyParam, Map<String, Object> value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> item : value.entrySet()) {
            setMapValue(keyParam, item.getKey(), item.getValue());
        }
    }

    @Override
    public void deleteMap(CacheKey keyParam) {
        String key = keyParam.parseKey();
        RedisTemplate<String, Object> redisTemplate = redisTemplate(keyParam.sourceKey());
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
        Set<String> keys = new HashSet<>();
        while (cursor.hasNext()) {
            Map.Entry<Object, Object> item = cursor.next();
            if (keys.size() == 50) {
                redisTemplate.opsForHash().delete(key, keys.toArray());
                keys.clear();
            }
            keys.add(item.getKey() + "");
        }
        if (!keys.isEmpty()) {
            redisTemplate.opsForHash().delete(key, keys.toArray());
        }
        redisTemplate.delete(key);
    }

    @Override
    public <T> void addList(CacheKey keyParam, List<T> value) {
        String key = keyParam.parseKey();
        redisTemplate(keyParam.sourceKey()).opsForList().rightPushAll(key, value.toArray());
    }

    @Override
    public void deleteList(CacheKey keyParam) {
        setExpire(keyParam, 0L);
    }

    @Override
    public <T> List<T> getList(CacheKey keyParam) {
        List<Object> resultList = redisTemplate(keyParam.sourceKey()).opsForList().range(keyParam.parseKey(), 0, 100);
        if (resultList == null || resultList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<T>((List) resultList);
    }

    @Override
    public <T> List<T> getList(CacheKey keyParam, int start, int end) {
        List<Object> resultList = redisTemplate(keyParam.sourceKey()).opsForList().range(keyParam.parseKey(), start, end);
        if (resultList == null || resultList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<T>((List) resultList);
    }

    @Override
    public <T> void addSet(CacheKey keyParam, Set<T> value) {
        String key = keyParam.parseKey();
        redisTemplate(keyParam.sourceKey()).opsForSet().add(key, value.toArray());
    }


    @Override
    public <T> Set<T> getSet(CacheKey keyParam, int count) {
        List values = redisTemplate(keyParam.sourceKey()).opsForSet().pop(keyParam.parseKey(), count);
        return new HashSet<>(values);
    }

    @Override
    public void deleteSet(CacheKey keyParam) {
        setExpire(keyParam, 0);
    }

    @Override
    public <T> void addSortedSet(CacheKey keyParam, List<SortedValue<T>> value) {
        String key = keyParam.parseKey();
        for (SortedValue<T> item : value) {
            redisTemplate(keyParam.sourceKey()).opsForZSet().add(key, item.getValue(), item.getScore());
        }
    }

    @Override
    public <T> List<SortedValue<T>> getSortedSet(CacheKey keyParam, int start, int end, boolean reversed) {
        RedisTemplate<String, Object> redisTemplate = redisTemplate(keyParam.sourceKey());
        String key = keyParam.parseKey();
        Set<ZSetOperations.TypedTuple<Object>> result = reversed ? redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end) : redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        if (result == null) {
            return Collections.EMPTY_LIST;
        }
        List<SortedValue<T>> list = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> item : result) {
            list.add(new SortedValue<T>((T) item.getValue(), item.getScore()));
        }
        return list;
    }

    @Override
    public void deleteSortedSet(CacheKey keyParam) {
        setExpire(keyParam, 0);
    }

    @Override
    public <T> void setEntity(CacheKey keyParam, T value) {
        setString(keyParam, value);
    }

    @Override
    public <T> T getEntity(CacheKey keyParam, Class<T> valueTypeClass) {
        Object result = redisTemplate(keyParam.sourceKey()).opsForValue().get(keyParam.parseKey());
        if (result instanceof Map) {
            return JsonUtil.mapToBean((Map) result, valueTypeClass);
        }
        return null;
    }

    @Override
    public void remove(CacheKey keyParam) {
        redisTemplate(keyParam.sourceKey()).delete(keyParam.parseKey());
    }

    @Override
    public boolean exists(CacheKey keyParam) {
        return redisTemplate(keyParam.sourceKey()).hasKey(keyParam.parseKey());
    }

    @Override
    public <T> T executeScript(CacheKey keyParam, String scriptText, Class<T> returnTypeClass, List<String> args, Collection values) {
        DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(returnTypeClass);
        redisScript.setScriptText(scriptText);
        return redisTemplate(keyParam.sourceKey()).execute(redisScript, args, values.toArray());
    }

    @Override
    public <T> T executeScriptFile(CacheKey keyParam, String path, Class<T> returnTypeClass, List<String> args, Collection values) {
        return executeScriptFile(keyParam, this.getClass(), path, returnTypeClass, args, values);
    }

    @Override
    public <T> T executeScriptFile(CacheKey keyParam, Class resourceClassType, String path, Class<T> returnTypeClass, List<String> args, Collection values) {
        DefaultRedisScript<T> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(returnTypeClass);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(path, resourceClassType.getClassLoader())));
        return redisTemplate(keyParam.sourceKey()).execute(redisScript, args, values.toArray());
    }

    @Override
    public long setExpire(CacheKey keyParam, long timeout) {
        List<String> keys = new ArrayList<>();
        List values = new ArrayList();
        keys.add(keyParam.parseKey());
        values.add(timeout);
        Long result = executeScript(keyParam, "if (redis.call('EXISTS', KEYS[1]) == 0) then\n" + "    return -1;\n" + "end\n" + "redis.call('PEXPIRE', KEYS[1], ARGV[1]);\n" + "return 1;", Long.class, keys, values);
        return result == null ? -1L : result;
    }

    @Override
    public Long increment(CacheKey keyParam, long delta) {
        String key = keyParam.parseKey();
        List<String> keys = new ArrayList<>();
        keys.add(key);
        List values = new ArrayList();
        values.add(keyParam.expire());
        values.add(delta);

        Object result = executeScript(keyParam, "if (redis.call('EXISTS', KEYS[1]) == 0) then\n" + "    redis.call('SET', KEYS[1], ARGV[2]);\n" + "    redis.call('PEXPIRE', KEYS[1], ARGV[1]);\n" + "    return redis.call('GET', KEYS[1]);\n" + "end\n" + "redis.call('SET', KEYS[1], redis.call('GET', KEYS[1]) + ARGV[2]);\n" + "return redis.call('GET', KEYS[1]);", Object.class, keys, values);
        return Long.parseLong(result + "");
    }

    @Override
    public void delete(CacheKey key) {
        redisTemplate(key.sourceKey()).delete(key.parseKey());
    }

    @Override
    public void deleteByPrefix(CacheKey key) {
        RedisTemplate<String, Object> redisTemplate = redisTemplate(key.sourceKey());
        Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match(key.parseKey() + "*").count(1000).build());
        List<String> keys = new ArrayList<>();
        while (cursor.hasNext()) {
            keys.add(cursor.next());
            if (keys.size() % 20 == 0) {
                redisTemplate.delete(keys);
                keys.clear();
            }
        }
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
