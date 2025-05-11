package com.oceancode.cloud.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestContext {
    private Map<String, Object> map = new ConcurrentHashMap<>();

    public <T> T get(String key) {
        return (T) map.get(key);
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }
}
