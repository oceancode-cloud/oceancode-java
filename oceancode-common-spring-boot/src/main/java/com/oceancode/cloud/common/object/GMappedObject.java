package com.oceancode.cloud.common.object;

import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.plugin.api.object.GTypeObject;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public abstract class GMappedObject<T> extends GTypeObject<T> {
    private T object;
    private Map<String, Object> map;

    public GMappedObject(T object) {
        super(object);
        this.object = object;
    }

    @Override
    public T object() {
        if (Objects.isNull(object)) {
            Map<String, Object> map = get();
            if (ValueUtil.isNotEmpty(map)) {
                object = JsonUtil.mapToBean(map, getTypeClass());
            }
        }
        return object;
    }

    @Override
    public Map<String, Object> get() {
        if (Objects.nonNull(map)) {
            return map;
        }
        map = JsonUtil.beanToMap(object());
        if (Objects.isNull(map)) {
            map = Collections.emptyMap();
        }
        return map;
    }

    public Class<T> getTypeClass() {
        if (Objects.nonNull(object)) {
            return (Class<T>) object.getClass();
        }
        return null;
    }
}
