package com.oceancode.cloud.test;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TestContext {
    private Map<String, Object> map = new ConcurrentHashMap<>();

    public <T> T get(String key, boolean required) {
        Object value = map.get(key);
        if (Objects.isNull(value) && required) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "key[" + key + "] is required.");
        }
        return (T) value;
    }

    public <T> T get(String key) {
        return get(key, true);
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }
}
