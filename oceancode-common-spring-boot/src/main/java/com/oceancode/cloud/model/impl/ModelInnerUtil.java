package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.ModelCacheService;

import java.util.Map;

public class ModelInnerUtil {
    protected static final ModelCacheService MODEL_CACHE_SERVICE;

    static {
        MODEL_CACHE_SERVICE = ComponentUtil.getBean(ModelCacheService.class, false);
    }

    private ModelInnerUtil() {
    }

    public static Object toBean(Map<String, Object> map) {
        if (ValueUtil.isEmpty(map)) {
            return null;
        }
        String classType = (String) map.get("_class");
        Class<?> dataTypeClass = MODEL_CACHE_SERVICE.getClass(classType);
        return JsonUtil.mapToBean(map, dataTypeClass);
    }
}
