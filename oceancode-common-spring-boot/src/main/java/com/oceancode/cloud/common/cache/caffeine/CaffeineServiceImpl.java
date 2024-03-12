/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.entity.SortedValue;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.CacheUtil;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnClass({Caffeine.class})
public final class CaffeineServiceImpl implements LocalCacheService {

    @Resource
    private CommonConfig commonConfig;

    private static Cache<String, Object> sessionCache;

    private static String sessionKey;

    @PostConstruct
    protected void init() {
        sessionKey = commonConfig.getValue(Config.Cache.SESSION_CACHE_KEY);
        if (ValueUtil.isNotEmpty(sessionKey)) {
            sessionCache = Caffeine.newBuilder()
                    .expireAfter(new CaffeineConfig.CaffeineExpiry())
                    .initialCapacity(100)
                    .maximumSize(1000)
                    .build();
        }
    }

    private static Cache<String, Object> getCache(CacheKey keyParam) {
        if (keyParam.key().equals(sessionKey)) {
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
        getCache(keyParam).policy().expireVariably().ifPresent(e -> {
            e.put(keyParam.parseKey(), finalValue, finalExpire, TimeUnit.MILLISECONDS);
        });

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
    public String getString(CacheKey keyParam) {
        String val = getVal(keyParam);
        if (Objects.isNull(val)) {
            if (CacheUtil.enabledAb(keyParam.key())) {
                val = (String) getCache(keyParam).getIfPresent(keyParam.parseBKey());
            }
        }
        if (Objects.isNull(val)) {
            return null;
        }
        if (CacheUtil.isEmpty(keyParam.key(), val)) {
            boolean enabledEmpty = CacheUtil.emptyEnabled(keyParam.key());
            if (enabledEmpty) {
                return null;
            }
        }
        return String.valueOf(val);
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
    public Map<String, Object> getMap(CacheKey keyParam) {
        Map<String, Object> map = getVal(keyParam);
        if (map == null) {
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public Map<String, Object> getMapValues(CacheKey keyParam, List<String> fields) {
        Map<String, Object> valueMap = getMap(keyParam);

        Map<String, Object> resultMap = new HashMap<>();
        for (String field : fields) {
            resultMap.put(field, valueMap.get(field));
        }
        return resultMap;
    }

    @Override
    public void setMapValue(CacheKey keyParam, String key, Object value) {
        Map<String, Object> map = getMap(keyParam);
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        map.put(key, value);
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
    public <T> List<T> getList(CacheKey keyParam) {
        List<T> list = getVal(keyParam);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public <T> List<T> getList(CacheKey keyParam, int start, int end) {
        List<T> list = getList(keyParam);
        if (list.isEmpty()) {
            return list;
        }
        if (end < list.size()) {
            return list.subList(start, end);
        }
        return Collections.emptyList();
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
    public <T> Set<T> getSet(CacheKey keyParam, int count) {
        Set<T> set = getVal(keyParam);
        if (set == null) {
            set = Collections.emptySet();
        }
        Set<T> resultSet = new HashSet<>();
        int pos = 0;
        Iterator<T> iterator = set.iterator();
        while (pos < count && iterator.hasNext()) {
            T t = iterator.next();
            resultSet.add(t);
            pos++;
        }
        return set;
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
    public <T> List<SortedValue<T>> getSortedSet(CacheKey keyParam, int start, int end, boolean reversed) {
        List<SortedValue<T>> list = (List<SortedValue<T>>) getCache(keyParam).asMap().get(keyParam.parseKey());
        if (Objects.isNull(list)) {
            return Collections.emptyList();
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
        return resultList;
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
    public <T> T getEntity(CacheKey keyParam, Class<T> valueTypeClass) {
        Object data = getVal(keyParam);
        if (data == null) {
            return null;
        }
        if (data instanceof String) {
            if (CacheUtil.isEmpty(keyParam.key(), (String) data)) {
                return null;
            }
        }
        return (T) data;
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
    public long setExpire(CacheKey keyParam, long timeout) {
        Object value = getCache(keyParam).getIfPresent(keyParam.parseKey());
        getCache(keyParam).policy().expireVariably().ifPresent(e -> {
            e.put(keyParam.parseKey(), value, keyParam.expire(), TimeUnit.MILLISECONDS);
        });
        return getCache(keyParam).policy().expireAfterWrite().get().getExpiresAfter(TimeUnit.MILLISECONDS);
    }

    @Override
    public Long increment(CacheKey keyParam, long delta) {
        synchronized (this) {
            Long value = (Long) getCache(keyParam).asMap().get(keyParam.parseKey());
            if (Objects.isNull(value)) {
                value = 0L;
            }
            value = value + delta;
            putVal(keyParam, value);
            return value;
        }
    }

    @Override
    public void delete(CacheKey key) {
        getCache(key).asMap().remove(key.parseKey());
    }
}
