package com.oceancode.cloud.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.oceancode.cloud.api.cache.DataCacheManager;
import com.oceancode.cloud.api.cache.LocalDataCacheManager;
import com.oceancode.cloud.common.cache.CommonCacheManager;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@ConditionalOnBean(CaffeineServiceImpl.class)
@Component
public class CaffeineDataCacheManager extends CommonCacheManager implements LocalDataCacheManager {
    private static Cache<String, Object> cache;

    @Resource
    private CommonConfig commonConfig;

    @PostConstruct
    protected void init() {
        cache = Caffeine.newBuilder()
                .expireAfter(new CaffeineConfig.CaffeineExpiry())
                .initialCapacity(100)
                .maximumSize(1000)
                .build();
    }

    protected Cache<String, Object> getCache() {
        return cache;
    }

    private void putVal(String key, Object value) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);
        getCache().policy().expireVariably().ifPresent(e -> {
            e.put(express, value, getExpire(cacheId), TimeUnit.MILLISECONDS);
        });
    }

    @Override
    public void setString(String key, Object value) {
        value = JsonUtil.toJson(value);
        putVal(key, value);
        Object finalValue = value;

        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);
        getCache().policy().expireVariably().ifPresent(e ->
                e.put(express, finalValue, getExpire(cacheId), TimeUnit.MILLISECONDS));
    }

    @Override
    public String getString(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);

        String val = (String) getCache().getIfPresent(express);
        if (Objects.isNull(val)) {
            return null;
        }
        if (Objects.equals(getEmptyValue(), val)) {
            return "";
        }
        return val;
    }

    @Override
    public void setObject(String key, Object value) {
        setString(key, JsonUtil.beanToMap(value));
    }

    private <T> T getVal(String express) {
        return (T) getCache().getIfPresent(express);
    }

    @Override
    public Map<String, Object> getObject(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);

        Object val = getVal(express);
        if (Objects.isNull(val)) {
            return null;
        }
        if (Objects.equals(val, getEmptyValue())) {
            return Collections.emptyMap();
        }
        if (val instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        } else if (val instanceof String str) {
            return JsonUtil.toBean(str, Map.class);
        }
        return null;
    }

    @Override
    public void setObjectList(String key, List<?> list) {
        putVal(key, list);
    }

    @Override
    public <T> List<T> getObjectList(String key, Class<T> dataType) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String cacheId = getCacheId(parts);

        Object val = getVal(express);
        if (Objects.isNull(val)) {
            return null;
        }
        if (Objects.equals(getEmptyValue(), val)) {
            return List.of();
        }
        return (List<T>) val;
    }

    @Override
    public String getSource() {
        return "master";
    }

    @Override
    public void deleteByPrefix(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        String pattern;
        if (express.endsWith("*")) {
            pattern = express.substring(0, parts.length - 1);
        } else {
            pattern = express;
        }
        ConcurrentMap<String, Object> map = getCache().asMap();
        map.keySet().stream().filter(it -> it.startsWith(pattern))
                .forEach(it -> {
                    getCache().asMap().remove(it);
                });
    }

    @Override
    public void delete(String key) {
        String[] parts = keyParts(key);
        String express = getExpress(parts, key);
        getCache().asMap().remove(express);
    }
}
