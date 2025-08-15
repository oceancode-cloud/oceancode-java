/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */
package com.oceancode.cloud.api.cache;

import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.cache.entity.SortedValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheService {

    void setString(CacheKey keyParam, Object value);

    Result<String> getString(CacheKey keyParam);

    <T> Result<List<T>> getStringAsList(CacheKey keyParam, Class<T> returnClassType);

    void setStringAsList(CacheKey key, List<?> list);

    void setMap(CacheKey keyParam, Map<String, Object> value);

    Result<Map<String, Object>> getMap(CacheKey keyParam);

    Result<Map<String, Object>> getMapValues(CacheKey keyParam, List<String> fields);

    void setMapValue(CacheKey keyParam, String key, Object value);

    void setMapValues(CacheKey keyParam, Map<String, Object> value);

    void deleteMap(CacheKey keyParam);

    <T> void addList(CacheKey keyParam, List<T> value);

    void deleteList(CacheKey keyParam);


    <T> Result<List<T>> getList(CacheKey keyParam);

    <T> Result<List<T>> getList(CacheKey keyParam, int start, int end);

    <T> void addSet(CacheKey keyParam, Set<T> value);

    <T> Result<Set<T>> getSet(CacheKey keyParam, int count);

    void deleteSet(CacheKey keyParam);

    void deleteSet(CacheKey keyParam, Set<String> values);

    <T> void addSortedSet(CacheKey keyParam, List<SortedValue<T>> value);

    <T> Result<List<SortedValue<T>>> getSortedSet(CacheKey keyParam, int start, int end, boolean reversed);

    void deleteSortedSet(CacheKey keyParam);

    <T> void setEntity(CacheKey keyParam, T value);

    <T> Result<T> getEntity(CacheKey keyParam, Class<T> valueTypeClass);

    void remove(CacheKey keyParam);

    boolean exists(CacheKey keyParam);

    Result<Long> setExpire(CacheKey keyParam, long timeout);

    Result<Long> increment(CacheKey keyParam, long delta);

    void delete(CacheKey key);

    void deleteByPrefix(CacheKey key);
}
