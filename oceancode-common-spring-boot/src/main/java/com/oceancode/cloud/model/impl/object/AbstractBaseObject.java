package com.oceancode.cloud.model.impl.object;

import com.oceancode.cloud.api.model.MObject;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.BaseObject;
import com.oceancode.cloud.model.ModelCacheService;
import com.oceancode.cloud.model.impl.ModelInnerUtil;

import java.util.Map;
import java.util.Objects;

public abstract class AbstractBaseObject<T extends MObject> implements BaseObject<T> {
    private T object;
    private Boolean isNull;

    protected static final ModelCacheService MODEL_CACHE_SERVICE;

    static {
        MODEL_CACHE_SERVICE = ComponentUtil.getBean(ModelCacheService.class, false);
    }

    private Map<String, Object> dataMap;

    public AbstractBaseObject(T object) {
        this.object = object;
    }

    public AbstractBaseObject(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public AbstractBaseObject() {
        this.isNull = true;
    }

    public T object() {
        if (Objects.isNull(object)) {
            if (ValueUtil.isEmpty(dataMap)) {
                return null;
            }
            synchronized (this) {
                if (ValueUtil.isEmpty(dataMap)) {
                    return null;
                }
                if (Objects.isNull(object)) {
                    object = (T) ModelInnerUtil.toBean(dataMap);
                }
            }
        }
        return object;
    }

    public boolean isNull() {
        return ValueUtil.isTrue(isNull);
    }

    public boolean isEmpty() {
        return !isNull() && Objects.isNull(object());
    }

    public boolean isPersist() {
        if (isNull()) {
            return false;
        }
        return Objects.nonNull(object());
    }

    public String versionId() {
        return isPersist() ? object().versionId() : null;
    }
}
