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
import com.oceancode.cloud.model.Model;
import com.oceancode.cloud.model.ModelField;
import com.oceancode.cloud.model.ModelService;
import com.oceancode.cloud.model.ModelUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CacheModelServiceImpl extends BaseModelServiceImpl implements ModelService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CacheModelServiceImpl.class);
    @Resource
    private CacheService cacheService;

    protected void saveModelFields(List<ModelField> fields) {
        if (ValueUtil.isEmpty(fields)) {
            return;
        }
        Map<String, Object> fieldMappings = new HashMap<>();
        for (ModelField modelField : fields) {
            fieldMappings.put(modelField.field(), modelField.id());
        }

        CacheKey key = buildModelFieldsKey(fields.get(0).object().modelId());
        cacheService.setMap(key, fieldMappings);
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

    @Override
    public Model findByModelId(String modelId) {
        return findByModelId(modelId, null);
    }

    @Override
    public Model findByModelId(String modelId, String versionId) {
        return findByModelId0(modelId, versionId);
    }

    private Model findByModelId0(String modelId, String versionId) {
        CacheKey key = buildModelInfoKey(modelId, versionId);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        if (!result.isSuccess()) {
            return null;
        }
        ModelImpl model = (ModelImpl) createModel(null);
        if (Objects.isNull(result.getResults())) {
            return model;
        }
        model.setDataMap(result.getResults());
        return model;
    }

    @Override
    public void save(Model model) {
        save0(model.object());
    }

    @Override
    public void save(MObject model) {
        save0(model);
    }

    private void save0(MObject model) {
        if (Objects.isNull(model)) {
            return;
        }
        CacheKey key = buildModelInfoKey(model.id(), model.versionId());
        cacheService.setMap(key, toMap(model));
        List<ModelField> fields = ((ModelImpl) model).getFields();
        if (Objects.isNull(fields)) {
            return;
        }

        for (ModelField field : fields) {
            if (Objects.nonNull(field)) {
                save(field);
            }
        }
    }

    private Map<String, Object> toMap(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        Map<String, Object> map = JsonUtil.beanToMap(value);
        if (ValueUtil.isEmpty(map)) {
            return null;
        }
        map.put("_class", value.getClass().getName());
        return map;
    }

    @Override
    public void save(ModelField modelField) {
        save0(modelField.object());
    }

    @Override
    public void save(MFieldObject modelField) {
        save0(modelField);
    }

    private void save0(MFieldObject modelField) {
        CacheKey key = buildModelFieldInfoKey(modelField.id(), modelField.versionId());
        cacheService.setMap(key, toMap(modelField));
    }

    @Override
    public Model createModel(MObject object) {
        ModelImpl model = new ModelImpl();
        model.setObject(object);
        return model;
    }

    @Override
    public ModelField createModelField(MFieldObject object) {
        ModelFieldImpl modelField = new ModelFieldImpl();
        modelField.setObject(object);
        return modelField;
    }

    @Override
    public void deleteByModelId(String modelId) {
        deleteByModelId(modelId, null);
    }

    @Override
    public void deleteByModelId(String modelId, String versionId) {
        deleteByModelId0(modelId, versionId);
    }

    @Override
    public void deleteByFieldId(String fieldId) {
        deleteByFieldId(fieldId, null);
    }

    @Override
    public void deleteByFieldId(String fieldId, String versionId) {
        deleteByFieldId0(fieldId, versionId);
    }

    private void deleteByFieldId0(String fieldId, String versionId) {
        cacheService.delete(buildModelFieldInfoKey(fieldId, versionId));
    }

    private void deleteByModelId0(String modelId, String versionId) {
        cacheService.delete(buildModelInfoKey(modelId, versionId));
    }

    @Override
    public ModelField findFieldById(String fieldId, String versionId) {
        Result<Map<String, Object>> result = cacheService.getMap(buildModelFieldInfoKey(fieldId, versionId));
        ModelFieldImpl modelField = (ModelFieldImpl) createModelField(null);
        if (!result.isSuccess()) {
            return null;
        }
        if (ValueUtil.isEmpty(result.getResults())) {
            return modelField;
        }
        modelField.setDataMap(result.getResults());
        return modelField;
    }

    private static CacheKey buildModelInfoKey(String modelId, String versionId) {
        if (ValueUtil.isEmpty(modelId)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "modelId is required.");
        }
        String express = getModelCachePrefix() + modelId;
        if (ValueUtil.isNotEmpty(versionId)) {
            express += ":v:" + versionId;
        }
        return KeyParam.of("model-info").express(express);
    }


    private static CacheKey buildModelFieldInfoKey(String fieldId, String versionId) {
        if (ValueUtil.isEmpty(fieldId)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "fieldId is required.");
        }
        String express = getModelCachePrefix() + "field:" + fieldId;
        if (ValueUtil.isNotEmpty(versionId)) {
            express += ":v:" + versionId;
        }
        return KeyParam.of("model-field-info").express(express);
    }

    private static CacheKey buildModelFieldsKey(String modelId) {
        if (ValueUtil.isEmpty(modelId)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "fieldId is required.");
        }
        String express = getModelCachePrefix() + "fields:" + modelId;
        return KeyParam.of("model-fields").express(express);
    }


    @Override
    protected List<ModelField> findModelFields(String modelId) {
        Result<Map<String, Object>> result = cacheService.getMap(buildModelFieldsKey(modelId));
        if (!result.isSuccess()) {
            return null;
        }

        if (ValueUtil.isEmpty(result.getResults())) {
            return Collections.emptyList();
        }
        List<ModelField> list = new ArrayList<>();
        for (Object value : result.getResults().values()) {
            if (Objects.isNull(value)) {
                continue;
            }
            String fieldId = null;
            if (value instanceof String v) {
                fieldId = v;
            } else {
                fieldId = value + "";
            }
            if (ValueUtil.isEmpty(fieldId)) {
                continue;
            }

            ModelField modelField = findFieldById(fieldId, null);
            if (Objects.nonNull(modelField)) {
                list.add(modelField);
            }
        }

        return list;
    }
}
