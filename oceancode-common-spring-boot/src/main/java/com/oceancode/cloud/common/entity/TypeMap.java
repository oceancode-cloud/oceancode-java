/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.entity;

import com.oceancode.cloud.common.util.TypeUtil;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeMap<K> implements Map<K, Object> {
    private Map<K, Object> data;

    public TypeMap() {
        this(new HashMap<>());
    }

    public TypeMap(Map<K, Object> data) {
        this.data = data;
    }

    public String getString(K key) {
        return getString(key, null);
    }

    public String getString(K key, String defaultValue) {
        return TypeUtil.convertToString(get(key), defaultValue);
    }

    public Long getLong(K key) {
        return getLong(key, null);
    }

    public Long getLong(K key, Long defaultValue) {
        return TypeUtil.convertToLong(get(key), defaultValue);
    }

    public Integer getInteger(K key) {
        return getInteger(key, null);
    }

    public Integer getInteger(K key, Integer defaultValue) {
        return TypeUtil.convertToInteger(get(key), defaultValue);
    }

    public BigDecimal getBigDecimal(K key) {
        return getBigDecimal(key, null);
    }

    public BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
        return TypeUtil.convertToBigDecimal(get(key), defaultValue);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    @Override
    public Object put(K key, Object value) {
        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ?> m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set<K> keySet() {
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public Set<Entry<K, Object>> entrySet() {
        return data.entrySet();
    }
}
