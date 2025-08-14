package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.ModelField;
import com.oceancode.cloud.model.ModelService;

import java.util.List;
import java.util.Map;

public abstract class BaseModelServiceImpl implements ModelService {
    private final static String MODEL_CACHE_PREFIX = "model:";

    protected static String getModelCachePrefix() {
        return MODEL_CACHE_PREFIX;
    }

    protected Object toBean(Map<String, Object> map) {
        if (ValueUtil.isEmpty(map)) {
            return null;
        }
        String classType = (String) map.get("_class");
        Class<?> aClass = getClass(classType);
        return JsonUtil.mapToBean(map, aClass);
    }

    protected List<ModelField> findModelFields(String modelId) {
        return List.of();
    }

    public ModelField findFieldById(String fieldId, String versionId) {
        return null;
    }

    @Override
    public ModelField findFieldById(String fieldId) {
        return findFieldById(fieldId, null);
    }
}
