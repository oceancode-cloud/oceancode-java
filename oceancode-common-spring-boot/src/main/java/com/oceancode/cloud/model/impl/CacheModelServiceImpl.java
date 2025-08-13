package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.api.MObject;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.ModelService;
import com.oceancode.cloud.model.ModelUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CacheModelServiceImpl implements ModelService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CacheModelServiceImpl.class);
    @Resource
    private CacheService cacheService;

    private final static String MODEL_CACHE_PREFIX = "model:info:";
    private final static String MODEL_INFO = "model-info";

    @Override
    public Result<MObject> findById(String id) {
        CacheKey key = buildModelInfoKey(id);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        if (!result.isSuccess()) {
            return CacheModelResult.NULL;
        }
        Map<String, Object> map = result.getResults();
        if (ValueUtil.isEmpty(map)) {
            return CacheModelResult.EMPTY;
        }
        return new CacheModelResult(toBean(map));
    }

    @Override
    public Result<List<MFieldObject>> findFields(String modelId, boolean allFields) {
        Result<List<MFieldObject>> result = findFields(modelId);
        if (!allFields) {
            return result;
        }
        List<MFieldObject> results = new ArrayList<>();

        if (ValueUtil.isNotEmpty(result.getResults())) {
            results.addAll(result.getResults());
        }
        int count = 0;
        while (count < 1000) {
            if (count > 0) {
                if (!result.isSuccess()) {
                    break;
                }
                if (ValueUtil.isEmpty(result.getResults())) {
                    break;
                }
                results.addAll(result.getResults());
            }

            Result<MObject> modelResult = findById(result.getResults().get(0).modelId());
            if (modelResult.isSuccess() || Objects.isNull(modelResult.getResults())) {
                break;
            }
            result = findFields0(modelResult.getResults().parentId());
            count++;
        }
        return new CacheModelResult<>(results);
    }

    @Override
    public Result<List<MFieldObject>> findFields(String modelId) {
        return findFields0(modelId);
    }

    private Result<List<MFieldObject>> findFields0(String modelId) {
        if (ValueUtil.isEmpty(modelId)) {
            return CacheModelResult.NULL;
        }
        CacheKey key = buildModelFieldsInfoKey(modelId);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        if (!result.isSuccess()) {
            return CacheModelResult.NULL;
        }
        Map<String, Object> map = result.getResults();
        if (ValueUtil.isEmpty(map)) {
            return CacheModelResult.EMPTY;
        }
        List<MFieldObject> fields = new ArrayList<>();
        for (Object value : map.values()) {
            if (value instanceof String fieldId) {
                CacheKey cacheKey = buildModelFieldInfoKey(fieldId);
                Result<Map<String, Object>> fieldResult = cacheService.getMap(cacheKey);
                if (!fieldResult.isSuccess() || Objects.isNull(fieldResult.getResults())) {
                    Result<MFieldObject> modelFieldResult = findFieldById(fieldId);
                    if (modelFieldResult.isSuccess() && Objects.nonNull(modelFieldResult.getResults())) {
                        fields.add(modelFieldResult.getResults());
                    }
                    continue;
                }
                Map<String, Object> fieldMap = fieldResult.getResults();
                MFieldObject bean = (MFieldObject) toBean(fieldMap);
                if (Objects.nonNull(bean)) {
                    fields.add(bean);
                }
            }
        }
        if (fields.isEmpty()) {
            return CacheModelResult.NULL;
        }
        return new CacheModelResult<>(fields);
    }


    @Override
    public Result<MFieldObject> findFieldById(String id) {
        return findFieldById0(id);
    }

    private Result<MFieldObject> findFieldById0(String id) {
        CacheKey key = buildModelFieldInfoKey(id);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        if (!result.isSuccess()) {
            return CacheModelResult.NULL;
        }
        Map<String, Object> map = result.getResults();
        return new CacheModelResult<>((MFieldObject) toBean(map));
    }

    @Override
    public Result<MFieldObject> findFieldByField(String modelId, String field) {
        CacheKey key = buildModelFieldsInfoKey(modelId);
        Result<Map<String, Object>> result = cacheService.getMapValues(key, Arrays.asList(field));
        if (!result.isSuccess()) {
            return CacheModelResult.NULL;
        }
        Map<String, Object> map = result.getResults();
        if (ValueUtil.isEmpty(map)) {
            return CacheModelResult.NULL;
        }
        Object object = map.get(field);
        if (Objects.isNull(object)) {
            return CacheModelResult.NULL;
        }
        return findFieldById(String.valueOf(object));
    }

    @Override
    public void addModelField(MFieldObject fieldObject) {
        addModelField0(fieldObject);
    }

    private void addModelField0(MFieldObject fieldObject) {
        CacheKey modelKey = buildModelFieldInfoKey(fieldObject.id());
        Map<String, Object> map = JsonUtil.beanToMap(fieldObject);
        map.put("_class", fieldObject.getClass().getName());
        cacheService.setMap(modelKey, map);

        CacheKey key = buildModelFieldsInfoKey(fieldObject.modelId());
        cacheService.setMapValue(key, fieldObject.field(), fieldObject.id());
    }

    @Override
    public void addModelFields(List<MFieldObject> fieldObjects) {
        if (ValueUtil.isEmpty(fieldObjects)) {
            return;
        }
        for (MFieldObject fieldObject : fieldObjects) {
            addModelField0(fieldObject);
        }
    }

    @Override
    public void updateModelField(MFieldObject fieldObject) {
        updateModelField0(fieldObject);
    }

    private void updateModelField0(MFieldObject fieldObject) {
        addModelField0(fieldObject);
    }

    @Override
    public void deleteModelField(String modelId, String field) {
        deleteModelField0(modelId, field);
    }

    private void deleteModelField0(String modelId, String field) {
        CacheKey key = buildModelFieldsInfoKey(modelId);

        Result<Map<String, Object>> result = cacheService.getMapValues(key, Arrays.asList(field));
        if (!result.isSuccess()) {
            return;
        }
        cacheService.delete(key);
        Map<String, Object> mapValues = result.getResults();
        if (ValueUtil.isNotEmpty(mapValues)) {
            String fieldId = (String) mapValues.get(field);
            if (Objects.isNull(fieldId)) {
                return;
            }
            deleteModelFieldById0(fieldId);
        }
    }

    @Override
    public void deleteModelFieldById(String id) {
        deleteModelFieldById0(id);
    }

    private void deleteModelFieldById0(String id) {
        deleteById0(id);
    }

    @Override
    public void update(MObject mObject) {
        update0(mObject);
    }

    private void update0(MObject mObject) {
        CacheKey key = buildModelInfoKey(mObject.id());
        Map<String, Object> map = JsonUtil.beanToMap(mObject);
        map.put("_class", mObject.getClass().getName());

        cacheService.setMap(key, map);
        if (ValueUtil.isNotEmpty(mObject.groupId())) {
            cacheService.addSet(buildModelInfoKey(mObject), Collections.singleton(mObject));
        }
    }

    @Override
    public void add(MObject mObject) {
        update0(mObject);
    }

    @Override
    public void deleteById(String id) {
        deleteById0(id);
    }

    private void deleteById0(String id) {
        cacheService.delete(buildModelInfoKey(id));
    }

    @Override
    public void deleteById(String id, String field) {
        cacheService.setMapValue(buildModelInfoKey(id), field, null);
    }

    @Override
    public boolean existsField(String modelId, String... fields) {
        return existsField0(modelId, fields);
    }

    private boolean existsField0(String modelId, String... fields) {
        if (ValueUtil.isEmpty(fields)) {
            return false;
        }
        Result<List<MFieldObject>> result = findFields0(modelId);
        if (!result.isSuccess()) {
            return false;
        }
        if (ValueUtil.isEmpty(result.getResults())) {
            return false;
        }

        Set<String> fieldList = Arrays.stream(fields).collect(Collectors.toSet());

        int count = 0;
        while (count < 1000) {
            boolean present = Objects.nonNull(result.getResults()) && result.getResults().stream().anyMatch(it -> fieldList.contains(it.field()));
            if (present) {
                return true;
            }
            if (ValueUtil.isEmpty(result.getResults())) {
                return false;
            }
            modelId = result.getResults().get(0).modelId();
            Result<MObject> modelResult = findById(modelId);
            if (!modelResult.isSuccess()) {
                return false;
            }
            if (Objects.isNull(modelResult.getResults())) {
                return false;
            }
            modelId = modelResult.getResults().parentId();
            result = findFields0(modelId);
            count++;
        }

        return false;
    }

    @Override
    public Class<?> getClass(String className) {
        if (ValueUtil.isEmpty(className)) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "className is required.");
        }
        Class<?> aClass = ModelUtil.getClass(className);
        if (Objects.isNull(aClass)) {
            synchronized (this) {
                if (Objects.isNull(ModelUtil.getClass(className))) {
                    try {
                        aClass = Class.forName(className);
                        ModelUtil.registerClass(aClass);
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("convert error.", e);
                    }
                }
            }
        }
        return aClass;
    }

    private Object toBean(Map<String, Object> map) {
        if (ValueUtil.isEmpty(map)) {
            return null;
        }
        String classType = (String) map.get("_class");
        Class<?> aClass = getClass(classType);
        return JsonUtil.mapToBean(map, aClass);
    }

    private CacheKey buildModelInfoKey(String id) {
        if (ValueUtil.isEmpty(id)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
        }
        return KeyParam.of(MODEL_INFO).express(MODEL_CACHE_PREFIX + id);
    }

    private CacheKey buildModelFieldInfoKey(String id) {
        if (ValueUtil.isEmpty(id)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
        }
        return KeyParam.of("model-field-info").express("model:field:info:" + id);
    }

    private CacheKey buildModelFieldsInfoKey(String id) {
        if (ValueUtil.isEmpty(id)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
        }
        return KeyParam.of("model-field").express(MODEL_CACHE_PREFIX + id + ":fields");
    }

    private CacheKey buildModelInfoKey(MObject mObject) {
        if (ValueUtil.isEmpty(mObject.id())) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
        }
        String express = MODEL_CACHE_PREFIX;
        if (ValueUtil.isNotEmpty(mObject.groupId())) {
            express += ":" + mObject.groupId() + ":";
        }
        express += mObject.id();
        return KeyParam.of("model-info").express(express);
    }

    protected <T> Result<T> createResult(Object data) {
        return createResult(data, true);
    }

    protected <T> Result<T> createResult(Object data, boolean success) {
        return (Result<T>) new CacheModelResult<>(data, success);
    }

    protected <T> Result<List<T>> createResult(List<?> data, boolean success) {
        return new CacheModelResult<>((List<T>) data, success);
    }
}
