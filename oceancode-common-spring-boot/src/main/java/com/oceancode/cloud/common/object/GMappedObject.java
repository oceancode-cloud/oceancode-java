package com.oceancode.cloud.common.object;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.plugin.api.object.GObject;
import com.oceancode.cloud.plugin.api.object.GTypeObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class GMappedObject<T> extends GTypeObject<T> {
    private T object;
    private Map<String, Object> map;
    private Boolean hasValue;
    private Class<?> dataTypeClass;

    public GMappedObject(T object) {
        super(object);
        this.object = object;
    }

    @Override
    public T object() {
        if (Objects.nonNull(hasValue)) {
            return object;
        }
        if (Objects.isNull(getObject())) {
            Map<String, Object> map = this.map;
            if (ValueUtil.isNotEmpty(map)) {
                Class<?> typeClass = getTypeClass();
                if (Objects.isNull(typeClass)) {
                    hasValue = false;
                    return object;
                }
                object = (T) JsonUtil.mapToBean(map, typeClass);
            }
        }
        hasValue = Objects.nonNull(object);
        return object;
    }

    protected T getObject() {
        return object;
    }

    @Override
    public Map<String, Object> get() {
        if (Objects.nonNull(map)) {
            return map;
        }
        map = JsonUtil.beanToMap(object());
        if (Objects.isNull(map)) {
            map = new HashMap<>();
            hasValue = null;
        }
        return map;
    }

    public Class<?> getTypeClass() {
        if (Objects.nonNull(dataTypeClass)) {
            return dataTypeClass;
        }
        Class<?> cur = this.getClass();
        while (!GMappedObject.class.equals(cur.getSuperclass())) {
            cur = cur.getSuperclass();
        }
        Type genericSuperclass = cur.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                Type actualTypeArgument = actualTypeArguments[0];
                if (actualTypeArgument instanceof Class<?> cls) {
                    dataTypeClass = cls;
                } else {
                    cur = this.getClass().getSuperclass();
                    if (cur.getGenericSuperclass() instanceof ParameterizedType pt) {
                        dataTypeClass = (Class<?>) pt.getActualTypeArguments()[0];
                    }
                }
            }
        }
        return dataTypeClass;
    }

    protected void setObject(T object) {
        this.object = object;
        hasValue = Objects.nonNull(object);
        this.map = null;
    }


    public void setProperty(String key, Object value) {
        get().put(key, value);
    }

    @Override
    public void setProperties(Map<String, Object> values) {
        if (Objects.isNull(values)) {
            return;
        }
        this.get().putAll(values);
        this.hasValue = null;
        this.object = null;
    }
}
