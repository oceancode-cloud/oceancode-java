package com.oceancode.cloud.test;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Parameter {
    private Map<String, Object> map = new HashMap<>();

    public Parameter(Map<String, Object> map) {
        if (Objects.nonNull(map)) {
            this.map.putAll(map);
        }
    }

    public Parameter add(String key, Object value) {
        if (Objects.isNull(value)) {
            return this;
        }
        map.put(key, value);
        return this;
    }

    public String getAsString(String key, String defaultValue) {
        Object object = map.get(key);
        if (Objects.isNull(object)) {
            return defaultValue;
        } else if (object instanceof String str) {
            if (ValueUtil.isEmpty(str)) {
                return defaultValue;
            }
            return str;
        }

        return object.toString();
    }

    public String getAsString(String key) {
        return getAsString(key, null);
    }

    public Long getAsLong(String key, Long defaultValue) {
        String v = getAsString(key);
        if (ValueUtil.isEmpty(v)) {
            return defaultValue;
        }
        return Long.parseLong(v);
    }

    public Long getAsLong(String key) {
        return getAsLong(key, null);
    }

    public Integer getAsInteger(String key, Integer defaultValue) {
        String v = getAsString(key);
        if (ValueUtil.isEmpty(v)) {
            return defaultValue;
        }
        return Integer.parseInt(v);
    }

    public Integer getAsInteger(String key) {
        return getAsInteger(key, null);
    }

    public Boolean getAsBoolean(String key, Boolean value) {
        String v = getAsString(key);
        if (ValueUtil.isEmpty(v)) {
            return value;
        }
        return Boolean.parseBoolean(v);
    }

    public Boolean getAsBoolean(String key) {
        return getAsBoolean(key);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public String getUsername() {
        return getAsString("username");
    }

    public String getPassword() {
        return getAsString("password");
    }

    public Long getId() {
        return getAsLong("id");
    }

    public Map<String, Object> getMap(String key) {
        Object object = map.get(key);
        if (object instanceof Map<?, ?>) {
            return (Map<String, Object>) object;
        }
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
