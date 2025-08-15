package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.indexer.IndexerItem;
import com.oceancode.cloud.api.indexer.ReferenceIndexer;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CacheModelServiceImpl extends BaseModelServiceImpl implements ModelService {
    private final static Logger LOGGER = LoggerFactory.getLogger(CacheModelServiceImpl.class);
    @Resource
    private CacheService cacheService;

    @Autowired
    private ReferenceIndexer referenceIndexer;

    protected void saveModelFields(List<ModelField> fields) {
        if (ValueUtil.isEmpty(fields)) {
            return;
        }
        Map<String, Object> fieldMappings = new HashMap<>();
        String modelId = fields.get(0).object().modelId();
        Set<IndexerItem> indexerItems = new HashSet<>();
        for (ModelField modelField : fields) {
            fieldMappings.put(modelField.field(), modelField.id());

            indexerItems.add(new IndexerItem(buildModelFieldIndexerType(modelField), modelField.id()));
        }

        CacheKey key = buildModelFieldsKey(modelId, fields.get(0).object().versionId());
        cacheService.setMap(key, fieldMappings);
        referenceIndexer.add(modelId, indexerItems);
    }

    private String buildModelFieldIndexerType(MFieldObject modelField) {
        return buildModelFieldIndexerType(modelField.versionId());
    }

    private String buildModelFieldIndexerType(ModelField modelField) {
        return buildModelFieldIndexerType(modelField.versionId());
    }

    private String buildModelFieldIndexerType(String versionId) {
        if (ValueUtil.isEmpty(versionId)) {
            return "field:v:" + versionId;
        }
        return "field";
    }

    @Override
    public void addIndexer(MFieldObject object) {
        Set<IndexerItem> indexerItems = new HashSet<>();
        indexerItems.add(new IndexerItem(buildModelFieldIndexerType(object), object.id()));

        referenceIndexer.add(object.modelId(), indexerItems);

        if (ValueUtil.isNotEmpty(object.refModelId())) {
            referenceIndexer.add(object.refModelId(), Collections.singleton(
                    new IndexerItem(buildModelFieldIndexerType(object), object.refModelId())
            ));
        }
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
        save0(model.object(), ((ModelImpl) model).getFields());
    }

    @Override
    public void save(MObject model) {
        save0(model, Collections.emptyList());
    }

    private void save0(MObject model, List<ModelField> fields) {
        if (Objects.isNull(model)) {
            return;
        }
        CacheKey key = buildModelInfoKey(model.id(), model.versionId());
        cacheService.setMap(key, toMap(model));
        if (Objects.isNull(fields)) {
            return;
        }

        for (ModelField field : fields) {
            if (Objects.nonNull(field)) {
                save0(field.object());
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

        referenceIndexer.add(modelField.modelId(),
                Collections.singleton(
                        new IndexerItem(buildModelFieldIndexerType(modelField), modelField.id())
                ));
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
        deleteByModelId0(modelId, null);
    }

    @Override
    public void deleteByModelId(String modelId, String versionId) {
        deleteByModelId0(modelId, versionId);
    }

    @Override
    public void deleteByFieldId(String fieldId) {
        deleteByFieldId0(fieldId, null);
    }

    @Override
    public void deleteByFieldId(String fieldId, String versionId) {
        deleteByFieldId0(fieldId, versionId);
    }

    private void deleteByFieldId0(String fieldId, String versionId) {
        ModelField modelField = findFieldById0(fieldId, versionId);
        cacheService.delete(buildModelFieldInfoKey(fieldId, versionId));
        if (Objects.nonNull(modelField) && Objects.nonNull(modelField.object())) {
            deleteModelFields(modelField.object().modelId());
        }
    }

    protected void deleteModelFields(String modelId) {
        if (ValueUtil.isEmpty(modelId)) {
            return;
        }
        CacheKey key = buildModelFieldsKey(modelId);
        cacheService.deleteMap(key);
    }

    private void deleteByModelId0(String modelId, String versionId) {
        cacheService.delete(buildModelInfoKey(modelId, versionId));
        cacheService.delete(buildModelFieldsKey(modelId, versionId));
        referenceIndexer.delete(new IndexerItem(buildModelFieldIndexerType(versionId), modelId));
    }

    @Override
    public ModelField findFieldById(String fieldId, String versionId) {
        return findFieldById0(fieldId, versionId);
    }

    private ModelField findFieldById0(String fieldId, String versionId) {
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
        return buildModelFieldsKey(modelId, null);
    }

    private static CacheKey buildModelFieldsKey(String modelId, String versionId) {
        if (ValueUtil.isEmpty(modelId)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "fieldId is required.");
        }
        String express = getModelCachePrefix() + "fields:" + modelId;
        if (ValueUtil.isNotEmpty(versionId)) {
            express += ":" + versionId;
        }
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
                // 有属性变更
                deleteModelFields(modelId);
                return null;
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
            } else {
                // 有属性变更
                deleteModelFields(modelId);
                return null;
            }
        }

        return list;
    }

    protected void delete(MFieldObject object) {
        if (ValueUtil.isNotEmpty(object.id())) {
            deleteByFieldId0(object.id(), object.versionId());
        }
        if (ValueUtil.isNotEmpty(object.modelId())) {
            Model model = findByModelId(object.modelId());
            CacheKey key = buildModelFieldsKey(object.modelId());
            cacheService.delete(key);
        }
    }
}
