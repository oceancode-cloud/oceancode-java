package com.oceancode.cloud.test.data;

import com.oceancode.cloud.common.entity.StringTypeMap;
import com.oceancode.cloud.common.util.JsonUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class Data {
    private String name;
    private String description;
    private Boolean positive;

    private Map<String, Object> datasets;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPositive() {
        return positive;
    }

    public void setPositive(Boolean positive) {
        this.positive = positive;
    }

    public Map<String, Object> getDatasets() {
        return datasets;
    }

    public void setDatasets(Map<String, Object> datasets) {
        this.datasets = datasets;
    }

    public boolean isPositive() {
        return Objects.nonNull(positive) && positive;
    }

    public Object get(String key) {
        if (datasets == null) {
            return null;
        }
        return datasets.get(key);
    }

    public String getString(String key, String defaultValue) {
        Object val = get(key);
        return val == null ? defaultValue : val.toString();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Long.parseLong(value + "");
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value + "");
    }

    public boolean hasData() {
        return Objects.nonNull(datasets) && !datasets.isEmpty();
    }

    public Map<String, Object> getMap(String key) {
        Object value = get(key);
        if (value == null || !(value instanceof Map)) {
            return Collections.emptyMap();
        }
        return (Map<String, Object>) value;
    }

    public <T> T getMap2Bean(String key, Class<T> typeClass) {
        return JsonUtil.mapToBean(getMap(key), typeClass);
    }

    @Override
    public String toString() {
        return (isPositive() ? "正例" : "反例") + "·" + name;
    }
}
