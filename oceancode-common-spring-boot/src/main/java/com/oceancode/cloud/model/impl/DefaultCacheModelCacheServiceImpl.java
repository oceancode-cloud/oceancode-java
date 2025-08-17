package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.model.MFieldObject;
import com.oceancode.cloud.api.model.MObject;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.Model;
import com.oceancode.cloud.model.ModelField;
import com.oceancode.cloud.model.ModelGroup;
import com.oceancode.cloud.model.ModelCacheService;
import com.oceancode.cloud.model.ModelUtil;
import com.oceancode.cloud.api.model.MGroupObject;
import com.oceancode.cloud.api.model.PersistModelService;
import com.oceancode.cloud.model.impl.object.ModelFieldImpl;
import com.oceancode.cloud.model.impl.object.ModelGroupImpl;
import com.oceancode.cloud.model.impl.object.ModelImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultCacheModelCacheServiceImpl implements ModelCacheService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultCacheModelCacheServiceImpl.class);
    private final static String MODEL = PersistModelService.MODEL;
    private final static String MODEL_FIELD = PersistModelService.MODEL_FIELD;
    private final static String MODEL_FIELDS = PersistModelService.MODEL_FIELDS;
    private final static String MODEL_GROUP = PersistModelService.MODEL_GROUP;

    @Resource
    private CacheService cacheService;

    private Map<String, PersistModelService> persistModelServicesMapping = new HashMap<>();

    public DefaultCacheModelCacheServiceImpl(List<PersistModelService> persistModelServices) {
        for (PersistModelService persistModelService : persistModelServices) {
            String scope = persistModelService.getScope();
            if (persistModelServicesMapping.containsKey(scope)) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, scope + " already exists. " + persistModelService.getClass().getName());
            }
            persistModelServicesMapping.put(scope, persistModelService);
        }
    }

    private PersistModelService getPersistModelService(String scope) {
        String businessScope = PersistModelService.BUSINESS_PREFIX + scope;
        return persistModelServicesMapping.getOrDefault(businessScope, persistModelServicesMapping.get(scope));
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
    public Model findModelById(String modelId) {
        return findModelById(modelId, null);
    }


    @Override
    public MObject findMObjectById(String id, String versionId, String scope) {
        CacheKey key = buildKey(id, scope, versionId);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        if (!result.isSuccess()) {
            PersistModelService persistModelService = getPersistModelService(scope);
            MObject mObject = persistModelService.findObjectById(id, versionId);
            if (Objects.nonNull(mObject)) {
                MObject object = (MObject) create(mObject);
                save0(mObject, false, true);
                return object;
            }
            return null;
        }

        return (MObject) ModelInnerUtil.toBean(result.getResults());
    }

    @Override
    public MObject findMObjectById(String id, String scope) {
        return findMObjectById(id, null, scope);
    }

    @Override
    public void saveMObjects(List<MObject> objects) {
        Map<String, Map<String, Object>> modelFieldMappings = new HashMap<>();
        for (MObject object : objects) {
            if (object instanceof MFieldObject o) {
                if (ValueUtil.isEmpty(o.modelId())) {
                    continue;
                }
                if (!modelFieldMappings.containsKey(o.modelId())) {
                    modelFieldMappings.put(o.modelId(), new HashMap<>());
                }
                modelFieldMappings.get(o.modelId()).put(o.id(), o.field());
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : modelFieldMappings.entrySet()) {
            String modelId = entry.getKey();
            CacheKey key = buildKey(modelId, MODEL_FIELDS, null);
            cacheService.setMap(key, entry.getValue());
        }
    }

    @Override
    public void deleteMObjectById(String id, String versionId, String scope) {
        CacheKey key = buildKey(id, scope, versionId);
        cacheService.delete(key);
    }

    @Override
    public void deleteMObjectById(String id, String scope) {
        deleteMObjectById(id, null, scope);
    }

    @Override
    public Model findModelById(String modelId, String versionId) {
        return (Model) findMObject(modelId, MODEL, versionId);
    }

    private Object findMObject(String modelId, String scope, String versionId) {
        CacheKey key = buildKey(modelId, scope, versionId);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        Map<String, Object> map = result.getResults();
        if (!result.isSuccess()) {
            map = null;
            PersistModelService persistModelService = getPersistModelService(scope);
            if (Objects.nonNull(persistModelService)) {
                MObject object = persistModelService.findObjectById(modelId, versionId);
                if (Objects.nonNull(object)) {
                    CacheKey cacheKey = buildKey(object.id(), scope, versionId);
                    map = toMap(object);
                    cacheService.setMap(cacheKey, map);
                }
            }
            return createObject(scope, map, Objects.isNull(map));
        }

        return createObject(scope, map, false);
    }

    private Object createObject(String scope, Map<String, Object> map, boolean isNull) {
        Object model = null;
        if (MODEL_FIELD.equals(scope)) {
            model = isNull ? ModelFieldImpl.NULL : new ModelFieldImpl(map);
        } else if (MODEL_GROUP.equals(scope)) {
            model = isNull ? ModelGroupImpl.NULL : new ModelGroupImpl(map);
        } else {
            model = isNull ? ModelImpl.NULL : new ModelImpl(map);
        }
        return model;
    }

    @Override
    public ModelField findModelFieldById(String fieldId) {
        return findModelFieldById(fieldId, null);
    }

    @Override
    public List<ModelField> findModelFields(String modelId) {
        return findModelFields(modelId, null);
    }

    @Override
    public ModelField findModelFieldById(String fieldId, String versionId) {
        return (ModelField) findMObject(fieldId, MODEL_FIELD, versionId);
    }

    @Override
    public List<ModelField> findModelFields(String modelId, String versionId) {
        CacheKey key = buildKey(modelId, MODEL_FIELDS, versionId);
        Result<Map<String, Object>> result = cacheService.getMap(key);
        Supplier<List<ModelField>> supplier = () -> {
            PersistModelService persistModelService = getPersistModelService(MODEL_FIELD);
            if (Objects.nonNull(persistModelService)) {
                List<MObject> modelFields = persistModelService.findObjects(modelId, versionId);
                Map<String, Object> map = new HashMap<>();
                List<ModelField> list = new ArrayList<>();
                if (ValueUtil.isNotEmpty(modelFields)) {
                    for (MObject modelField : modelFields) {
                        if (!(modelField instanceof MFieldObject fieldObject)) {
                            continue;
                        }
                        String value = modelField.id();
                        if (ValueUtil.isNotEmpty(modelField.versionId())) {
                            value += ":" + modelField.versionId();
                        }
                        map.put(value, fieldObject.field());
                        list.add((ModelField) create(modelField));
                    }
                }
                cacheService.setMap(key, map);
                return list;
            }
            return Collections.emptyList();
        };
        if (!result.isSuccess()) {
            return supplier.get();
        }

        List<ModelField> list = new ArrayList<>();

        PersistModelService persistModelService = null;
        boolean hasPersistModelService = false;
        for (Map.Entry<String, Object> entry : result.getResults().entrySet()) {
            boolean ret = !Objects.isNull(entry.getValue());
            String fieldName = entry.getValue() + "";
            if (ValueUtil.isEmpty(fieldName)) {
                ret = false;
            }
            if (!ret) {
                cacheService.delete(key);
                return supplier.get();
            }
            String vId = null;
            String keyId = entry.getKey();
            if (keyId.contains(":")) {
                String[] split = keyId.split(":");
                keyId = split[0];
                vId = split[1];
            }
            if (!hasPersistModelService) {
                hasPersistModelService = true;
                persistModelService = getPersistModelService(MODEL_FIELD);
            }
            if (Objects.nonNull(persistModelService)) {
                MObject modelField = persistModelService.findObjectById(keyId, vId);
                if (Objects.isNull(modelField)) {
                    cacheService.delete(key);
                    return supplier.get();
                }
                list.add((ModelField) create(modelField));
            } else {
                return Collections.emptyList();
            }
        }
        return list;
    }

    @Override
    public ModelGroup findModelGroupById(String groupId) {
        return findModelGroupById(groupId, null);
    }

    @Override
    public ModelGroup findModelGroupById(String groupId, String versionId) {
        return (ModelGroup) findMObject(groupId, MODEL_GROUP, versionId);
    }

    @Override
    public void save(MObject object, boolean throwEx) {
        save0(object, throwEx, false);
    }

    private void save0(MObject object, boolean throwEx, boolean saveCache) {
        if (Objects.isNull(object)) {
            if (!saveCache && throwEx) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "object is required.");
            }
            return;
        }

        String scope = MODEL;
        if (object instanceof MGroupObject) {
            scope = MODEL_GROUP;
        } else if (object instanceof MFieldObject) {
            scope = MODEL_FIELD;
        }

        if (!saveCache && ValueUtil.isNotEmpty(object.id())) {
            PersistModelService persistModelService = getPersistModelService(scope);
            if (Objects.nonNull(persistModelService)) {
                persistModelService.saveObject(object, throwEx);
                if (object instanceof MFieldObject o) {
                    if (ValueUtil.isNotEmpty(o.modelId())) {
                        cacheService.delete(buildKey(object.id(), MODEL_FIELDS, o.modelId()));
                    }
                }
            } else {
                if (throwEx) {
                    throw new BusinessRuntimeException(CommonErrorCode.ERROR, "not found PersistModelService to save object");
                }
            }
            return;
        }

        CacheKey key = buildKey(object.id(), scope, object.versionId());

        if (object instanceof MFieldObject o) {
            if (ValueUtil.isNotEmpty(o.modelId())) {
                cacheService.delete(buildKey(object.id(), MODEL_FIELDS, o.modelId()));
            }
        }

        cacheService.delete(key);
    }

    @Override
    public void save(MObject object) {
        save(object, true);
    }

    @Override
    public void deleteModelById(String id) {
        deleteModelById(id, null);
    }

    @Override
    public void deleteModelById(String id, String versionId) {
        delete(id, MODEL, versionId);
    }

    @Override
    public void deleteModelFieldById(String fieldId) {
        deleteModelFieldById(fieldId, null);
    }

    @Override
    public void deleteModelFieldById(String fieldId, String versionId) {
        ModelField modelField = findModelFieldById(fieldId, versionId);
        if (Objects.nonNull(modelField)) {
            delete(modelField.id(), MODEL_FIELDS, versionId);
        }
        delete(fieldId, MODEL_FIELD, versionId);
    }

    @Override
    public void deleteModelGroupById(String groupId) {
        deleteModelGroupById(groupId, null);
    }

    @Override
    public void deleteModelGroupById(String groupId, String versionId) {
        delete(groupId, MODEL_GROUP, versionId);
    }

    private void delete(String id, String scope, String versionId) {
        if (ValueUtil.isEmpty(id)) {
            return;
        }
        PersistModelService persistModelService = getPersistModelService(scope);
        if (Objects.nonNull(persistModelService)) {
            persistModelService.deleteObject(id, versionId);
        }
        CacheKey key = buildKey(id, scope, versionId);
        cacheService.delete(key);
    }

    @Override
    public Object create(MObject object) {
        Object result = null;
        if (object instanceof MFieldObject) {
            result = new ModelFieldImpl((MFieldObject) object);
        } else if (object instanceof MGroupObject) {
            result = new ModelGroupImpl((MGroupObject) object);
        } else {
            result = new ModelImpl(object);
        }
        return result;
    }

    protected static String getModelCachePrefix() {
        return "model:";
    }

    private static CacheKey buildKey(String id, String scope, String versionId) {
        if (ValueUtil.isEmpty(id)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "fieldId is required.");
        }
        String express = getModelCachePrefix();
        String keyId = "model";
        if (ValueUtil.isNotEmpty(scope)) {
            express += scope + ":";
            keyId += "-" + scope;
        }
        express += id;
        if (ValueUtil.isNotEmpty(versionId)) {
            express += ":" + versionId;
        }

        return KeyParam.of(keyId).express(express);
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


}
