/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.oceancode.cloud.api.LockActionCallback;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.entity.SortedValue;
import com.oceancode.cloud.common.cache.CacheResult;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.CacheUtil;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
@ConditionalOnClass({Caffeine.class})
public final class CaffeineServiceImpl implements LocalCacheService {

    @Resource
    private CommonConfig commonConfig;

    private static Cache<String, Object> sessionCache;

    private static String sessionKey;

    @PostConstruct
    protected void init() {
        sessionKey = commonConfig.getValue(Config.Cache.SESSION_CACHE_KEY, "default");
        if (ValueUtil.isNotEmpty(sessionKey)) {
            sessionCache = Caffeine.newBuilder()
                    .expireAfter(new CaffeineConfig.CaffeineExpiry())
                    .initialCapacity(100)
                    .maximumSize(1000)
                    .build();
        }
    }

    private static Cache<String, Object> getCache(CacheKey keyParam) {
        if (sessionKey.equals(keyParam.sourceKey()) || "master".equals(keyParam.sourceKey()) || ValueUtil.isEmpty(keyParam.sourceKey())) {
            return sessionCache;
        }
        return ComponentUtil.getBean("caffeineDefaultCache", Cache.class);
    }

    @Override
    public void setString(CacheKey keyParam, Object value) {
        long expire = keyParam.expire();
        if (value == null) {
            if (!CacheUtil.emptyEnabled(keyParam.key())) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "value must not null");
            }
            value = CacheUtil.emptyValue(keyParam.key());
            expire = CacheUtil.emptyExpire(keyParam.key());
        }
        putVal(keyParam, value);
        Object finalValue = value;
        long finalExpire = expire;
        getCache(keyParam).policy().expireVariably().ifPresent(e ->
                e.put(keyParam.parseKey(), finalValue, finalExpire, TimeUnit.MILLISECONDS));

        if (CacheUtil.enabledAb(keyParam.key())) {
            getCache(keyParam).asMap().remove(keyParam.parseBKey());
        }
    }

    private void putVal(CacheKey keyParam, Object value) {
        getCache(keyParam).policy().expireVariably().ifPresent(e -> {
            e.put(keyParam.parseKey(), value, keyParam.expire(), TimeUnit.MILLISECONDS);
        });
    }

    @Override
    public Result<String> getString(CacheKey keyParam) {
        String val = getVal(keyParam);
        if (Objects.isNull(val)) {
            if (CacheUtil.enabledAb(keyParam.key())) {
                val = (String) getCache(keyParam).getIfPresent(keyParam.parseBKey());
            }
        }
        if (Objects.isNull(val)) {
            return CacheResult.NULL;
        }
        if (CacheUtil.isEmpty(keyParam.key(), val)) {
            boolean enabledEmpty = CacheUtil.emptyEnabled(keyParam.key());
            if (enabledEmpty) {
                return CacheResult.EMPTY;
            }
        }
        return new CacheResult<>(val);
    }

    @Override
    public <T> Result<List<T>> getStringAsList(CacheKey keyParam, Class<T> returnClassType) {
        Object value = getVal(keyParam);
        if (null == value && CacheUtil.enabledAb(keyParam.key())) {
            value = getCache(keyParam).getIfPresent(keyParam.parseBKey());
        }
        if (Objects.isNull(value)) {
            return CacheResult.NULL;
        }
        if (CacheUtil.isEmpty(keyParam.key(), value)) {
            return CacheResult.EMPTY;
        }
        return new CacheResult<>((List<T>) value);
    }

    @Override
    public void setStringAsList(CacheKey key, List<?> value) {
        if (value == null) {
            if (!CacheUtil.emptyEnabled(key.key())) {
                return;
            }
            value = Collections.emptyList();
        }
        putVal(key, value);
    }

    private <T> T getVal(CacheKey keyParam) {
        return (T) getCache(keyParam).getIfPresent(keyParam.parseKey());
    }

    @Override
    public void setMap(CacheKey keyParam, Map<String, Object> value) {
        if (value == null) {
            if (!CacheUtil.emptyEnabled(keyParam.key())) {
                return;
            }
            value = Collections.emptyMap();
        }
        putVal(keyParam, value);
    }

    @Override
    public Result<Map<String, Object>> getMap(CacheKey keyParam) {
        Map<String, Object> map = getVal(keyParam);
        if (map == null) {
            return CacheResult.NULL;
        }
        return new CacheResult<>(map);
    }

    @Override
    public Result<Map<String, Object>> getMapValues(CacheKey keyParam, List<String> fields) {
        Result<Map<String, Object>> valueMap = getMap(keyParam);
        if (!valueMap.isSuccess()) {
            return CacheResult.NULL;
        }

        Map<String, Object> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, valueMap.getResults().get(field));
        }
        return new CacheResult<>(resultMap);
    }

    @Override
    public void setMapValue(CacheKey keyParam, String key, Object value) {
        Result<Map<String, Object>> result = getMap(keyParam);
        Map<String, Object> map = result.getResults();
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        if (Objects.isNull(value)) {
            map.remove(key);
        } else {
            map.put(key, value);
        }

        setMap(keyParam, map);
    }

    @Override
    public void setMapValues(CacheKey keyParam, Map<String, Object> value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        Result<Map<String, Object>> result = getMap(keyParam);
        Map<String, Object> map = result.getResults();
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        map.putAll(value);
        setMap(keyParam, map);
    }

    @Override
    public void deleteMap(CacheKey keyParam) {
        getCache(keyParam).asMap().remove(keyParam.parseKey());
    }


    @Override
    public <T> void addList(CacheKey keyParam, List<T> value) {
        if (value == null) {
            if (!CacheUtil.emptyEnabled(keyParam.key())) {
                return;
            }
            value = Collections.emptyList();
        }
        putVal(keyParam, value);
    }

    @Override
    public void deleteList(CacheKey keyParam) {
        getCache(keyParam).asMap().remove(keyParam.parseKey());
    }

    @Override
    public <T> Result<List<T>> getList(CacheKey keyParam) {
        List<T> list = getVal(keyParam);
        if (list == null) {
            return CacheResult.NULL;
        }
        return new CacheResult<>(list);
    }

    @Override
    public <T> Result<List<T>> getList(CacheKey keyParam, int start, int end) {
        Result<List<Object>> result = getList(keyParam);
        if (!result.isSuccess()) {
            return CacheResult.NULL;
        }
        List<Object> list = result.getResults();
        if (ValueUtil.isEmpty(result.getResults())) {
            return CacheResult.EMPTY;
        }
        if (end < list.size()) {
            return new CacheResult<>((List<T>) list.subList(start, end));
        }
        return CacheResult.EMPTY;
    }

    @Override
    public <T> void addSet(CacheKey keyParam, Set<T> value) {
        if (value == null) {
            if (!CacheUtil.emptyEnabled(keyParam.key())) {
                return;
            }
            value = Collections.emptySet();
        }
        putVal(keyParam, value);
    }

    @Override
    public <T> Result<Set<T>> getSet(CacheKey keyParam, int count) {
        Set<T> set = getVal(keyParam);
        if (set == null) {
            return CacheResult.NULL;
        }
        Set<T> resultSet = new HashSet<>();
        int pos = 0;
        Iterator<T> iterator = set.iterator();
        while (pos < count && iterator.hasNext()) {
            T t = iterator.next();
            resultSet.add(t);
            pos++;
        }
        return new CacheResult<>(set);
    }

    @Override
    public void deleteSet(CacheKey keyParam) {
        getCache(keyParam).asMap().remove(keyParam.parseKey());
    }

    @Override
    public <T> void addSortedSet(CacheKey keyParam, List<SortedValue<T>> value) {
        if (value == null) {
            if (!CacheUtil.emptyEnabled(keyParam.key())) {
                return;
            }
            value = Collections.emptyList();
        }
        putVal(keyParam, value);
    }

    @Override
    public <T> Result<List<SortedValue<T>>> getSortedSet(CacheKey keyParam, int start, int end, boolean reversed) {
        List<SortedValue<T>> list = (List<SortedValue<T>>) getCache(keyParam).asMap().get(keyParam.parseKey());
        if (Objects.isNull(list)) {
            return CacheResult.NULL;
        }
        Iterator<SortedValue<T>> iterator = list.iterator();
        for (int i = 0; i < start; i++) {
            iterator.next();
        }
        int pos = start;
        List<SortedValue<T>> resultList = new ArrayList<>(end - start);
        while (pos < end && iterator.hasNext()) {
            SortedValue<T> item = iterator.next();
            resultList.add(item);
        }
        return new CacheResult<>(resultList);
    }

    @Override
    public void deleteSortedSet(CacheKey keyParam) {
        getCache(keyParam).asMap().remove(keyParam.parseKey());
    }

    @Override
    public <T> void setEntity(CacheKey keyParam, T value) {
        if (value == null) {
            if (!CacheUtil.emptyEnabled(keyParam.key())) {
                return;
            }
            putVal(keyParam, CacheUtil.emptyValue(keyParam.key()));
            return;
        }
        putVal(keyParam, value);
    }

    @Override
    public <T> Result<T> getEntity(CacheKey keyParam, Class<T> valueTypeClass) {
        Object data = getVal(keyParam);
        if (data == null) {
            return CacheResult.NULL;
        }
        if (data instanceof String) {
            if (CacheUtil.isEmpty(keyParam.key(), (String) data)) {
                return CacheResult.EMPTY;
            }
        }
        return new CacheResult<>((T) data);
    }

    @Override
    public void remove(CacheKey keyParam) {
        getCache(keyParam).asMap().remove(keyParam.parseKey());
    }

    @Override
    public boolean exists(CacheKey keyParam) {
        return getCache(keyParam).getIfPresent(keyParam.parseKey()) != null;
    }

    @Override
    public Result<Long> setExpire(CacheKey keyParam, long timeout) {
        Object value = getCache(keyParam).getIfPresent(keyParam.parseKey());
        if (Objects.isNull(value)) {
            return new CacheResult<>(-1L, false);
        }
        getCache(keyParam).policy().expireVariably().ifPresent(e -> {
            e.put(keyParam.parseKey(), value, keyParam.expire(), TimeUnit.MILLISECONDS);
        });
        return new CacheResult<>(getCache(keyParam).policy().expireAfterWrite().get().getExpiresAfter(TimeUnit.MILLISECONDS));
    }

    @Override
    public Result<Long> increment(CacheKey keyParam, long delta) {
        synchronized (this) {
            Long value = (Long) getCache(keyParam).asMap().get(keyParam.parseKey());
            if (Objects.isNull(value)) {
                value = 0L;
            }
            value = value + delta;
            putVal(keyParam, value);
            return new CacheResult<>(value);
        }
    }

    @Override
    public void delete(CacheKey key) {
        getCache(key).asMap().remove(key.parseKey());
    }

    @Override
    public void deleteByPrefix(CacheKey key) {
        ConcurrentMap<String, Object> map = getCache(key).asMap();
        String rKey = key.parseKey();
        List<String> keys = map.keySet().stream().filter(e -> e.startsWith(rKey))
                .collect(Collectors.toList());
        for (String mKey : keys) {
            map.remove(mKey);
        }
    }

    @Override
    public void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback) {
        ConcurrentMap<String, Object> map = getCache(cacheKey).asMap();
        final long threadId = Thread.currentThread().getId();
        String key = threadId + "";
        ReentrantLock lock = (ReentrantLock) map.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            lock.tryLock(timeout, TimeUnit.MILLISECONDS);
            callback.doAction();
        } catch (InterruptedException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        } finally {
            lock.unlock();
        }
    }
}
