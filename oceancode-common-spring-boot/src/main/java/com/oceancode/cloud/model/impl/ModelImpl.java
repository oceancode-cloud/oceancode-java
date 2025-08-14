package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.api.MObject;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.Model;
import com.oceancode.cloud.model.ModelField;
import com.oceancode.cloud.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ModelImpl implements Model {
    private transient MObject object;
    private transient Map<String, Object> dataMap = new HashMap<>();

    private transient List<ModelField> fields;

    private static final BaseModelServiceImpl modelService;

    static {
        modelService = (BaseModelServiceImpl) ComponentUtil.getBean(ModelService.class);
    }


    @Override
    public MObject object() {
        if (Objects.isNull(object)) {
            if (ValueUtil.isEmpty(dataMap)) {
                return null;
            }
            synchronized (this) {
                if (ValueUtil.isEmpty(dataMap)) {
                    return null;
                }
                if (Objects.isNull(object)) {
                    object = (MObject) modelService.toBean(dataMap);
                }
            }
        }
        return object;
    }

    @Override
    public void addField(ModelField modelField) {
        if (Objects.isNull(fields)) {
            fields = new ArrayList<>();
        }
        fields.add(modelField);
    }

    @Override
    public void addField(MFieldObject object) {
        ModelField modelField = modelService.createModelField(object);
        if (Objects.nonNull(modelField)) {
            addField(modelField);
        }
    }

    public List<ModelField> getFields() {
        return fields;
    }

    @Override
    public List<Model> findReferenced() {
        return List.of();
    }

    @Override
    public String id() {
        return isPersist() ? object().id() : null;
    }

    @Override
    public String versionId() {
        return isPersist() ? object().versionId() : null;
    }

    @Override
    public Model parent() {
        if (!isPersist()) {
            return null;
        }
        if (ValueUtil.isEmpty(object().parentId())) {
            return null;
        }
        return modelService.findByModelId(object().parentId());
    }

    @Override
    public List<Model> parents() {
        List<Model> list = new ArrayList<>();
        Model parent = parent();
        while (Objects.nonNull(parent)) {
            list.add(parent);
            parent = parent.parent();
        }
        return list;
    }


    @Override
    public ModelField field(String field) {
        if (ValueUtil.isEmpty(field)) {
            return null;
        }
        List<ModelField> list = new ArrayList<>();
        collectFields(list, this, true, fields -> fields.stream().anyMatch(f -> Objects.equals(f.field(), field)));
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public ModelField findFieldById(String fieldId) {
        return findFieldById(fieldId, null);
    }

    @Override
    public ModelField findFieldById(String fieldId, String versionId) {
        return modelService.findFieldById(fieldId, versionId);
    }

    @Override
    public List<ModelField> fields(boolean allFields) {
        List<ModelField> list = new ArrayList<>();
        collectFields(list, this, allFields, null);

        return list;
    }

    private static List<ModelField> listFields(String modelId) {
        List<ModelField> modelFields = modelService.findModelFields(modelId);
        return modelFields;
    }

    private void collectFields(List<ModelField> list, Model model, boolean allFields, Function<List<ModelField>, Boolean> nextFunction) {
        if (Objects.isNull(model)) {
            return;
        }
        List<ModelField> modelFields = listFields(model.id());
        list.addAll(modelFields);

        if (Objects.nonNull(nextFunction)) {
            if (ValueUtil.isFalse(nextFunction.apply(list))) {
                return;
            }
        }

        if (allFields) {
            collectFields(list, model.parent(), allFields, nextFunction);
        }
    }

    @Override
    public List<ModelField> fields() {
        return fields(false);
    }

    @Override
    public boolean hasParent() {
        return Objects.nonNull(parent());
    }

    public void setObject(MObject object) {
        this.object = object;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        if (Objects.isNull(dataMap)) {
            return;
        }
        this.dataMap = dataMap;
    }

    @Override
    public boolean isPersist() {
        return Objects.nonNull(object());
    }


    @Override
    public boolean isEnum() {
        return isPersist() && "enum".equalsIgnoreCase(object().type());
    }

    @Override
    public List<Model> refModels(boolean isList) {
        List<ModelField> list = fields();
        return list.stream().filter(it -> {
            if (isList) {
                return it.isRefList();
            }
            return it.isRef();
        }).map(it -> it.ref()).toList();
    }
}
