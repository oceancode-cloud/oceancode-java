package com.oceancode.cloud.test;

import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class StubObj<T> {
    private T obj;
    private Map<Field, Object> fieldOldValueCacheMap = new HashMap<>();

    private StubObj(T obj) {
        this.obj = obj;
    }

    public static <T> StubObj<T> stub(T obj) {
        return new StubObj<>(obj);
    }

    private StubObj<T> mockFields(Function<Field, Object> callback, String... mockFields) {
        return mockFields(callback, null, mockFields);
    }

    private StubObj<T> mockFields(Function<Field, Object> callback, Function<Field, Boolean> matchFunction, String... mockFields) {
        List<String> list = Arrays.stream(mockFields).toList();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            boolean matched = false;
            if (Objects.nonNull(matchFunction)) {
                Boolean ret = matchFunction.apply(declaredField);
                if (Objects.nonNull(ret) && ret) {
                    matched = true;
                }
            } else {
                matched = list.contains(declaredField.getName());
            }
            if (matched) {
                declaredField.setAccessible(true);
                Object value = callback.apply(declaredField);
                if (!fieldOldValueCacheMap.containsKey(declaredField)) {
                    try {
                        fieldOldValueCacheMap.put(declaredField, declaredField.get(obj));
                        declaredField.set(obj, value);
                    } catch (IllegalAccessException e) {

                    }
                }
            }
        }

        return this;
    }

    public StubObj<T> mockFields(String... fields) {
        return mockFields(field -> Mockito.mock(field.getType()), fields);
    }

    public StubObj<T> mockField(String field, Object value) {
        return mockFields(fd -> value, field);
    }

    public StubObj<T> mockField(Class<?> typeClass, Object value) {
        return mockFields(field -> value, field -> field.getType().equals(typeClass));
    }

    public T get() {
        return obj;
    }

    public void reset() {
        for (Map.Entry<Field, Object> entry : fieldOldValueCacheMap.entrySet()) {
            if (Objects.isNull(entry.getKey())) {
                continue;
            }
            try {
                entry.getKey().set(obj, entry.getValue());
            } catch (IllegalAccessException e) {

            }
        }
    }
}
