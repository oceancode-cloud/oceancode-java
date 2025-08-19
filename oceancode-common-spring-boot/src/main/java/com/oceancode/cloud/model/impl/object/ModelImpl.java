package com.oceancode.cloud.model.impl.object;

import com.oceancode.cloud.api.model.MFieldObject;
import com.oceancode.cloud.api.model.MObject;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.Model;
import com.oceancode.cloud.model.ModelField;
import com.oceancode.cloud.model.ModelGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ModelImpl extends AbstractBaseObject<MObject> implements Model {
    public static final ModelImpl NULL = new ModelImpl();
    private transient List<ModelField> fields;

    public ModelImpl(MObject object) {
        super(object);
    }

    public ModelImpl(Map<String, Object> dataMap) {
        super(dataMap);
    }

    public ModelImpl() {

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
        MODEL_CACHE_SERVICE.save(object, true);
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
    public Model parent() {
        if (!isPersist()) {
            return NULL;
        }
        if (ValueUtil.isEmpty(object().parentId()) || "0".equals(object().parentId())) {
            return NULL;
        }
        return MODEL_CACHE_SERVICE.findModelById(object().parentId());
    }

    @Override
    public List<Model> parents() {
        List<Model> list = new ArrayList<>();
        Model parent = parent();
        while (Objects.nonNull(parent) && parent.isPersist()) {
            list.add(parent);
            parent = parent.parent();
        }
        return list;
    }


    @Override
    public ModelField field(String field) {
        if (ValueUtil.isEmpty(field)) {
            return ModelFieldImpl.NULL;
        }
        List<ModelField> list = new ArrayList<>();
        collectFields(list, this, true, f -> Objects.equals(f.field(), field));
        if (list.isEmpty()) {
            return ModelFieldImpl.NULL;
        }
        return list.get(0);
    }

    @Override
    public MFieldObject fieldObject(String field) {
        ModelField modelField = field(field);
        if (Objects.nonNull(modelField)) {
            return modelField.object();
        }
        return null;
    }

    @Override
    public ModelField findFieldById(String fieldId) {
        return findFieldById(fieldId, null);
    }

    @Override
    public ModelField findFieldById(String fieldId, String versionId) {
        return MODEL_CACHE_SERVICE.findModelFieldById(fieldId, versionId);
    }

    @Override
    public List<ModelField> fields(boolean allFields) {
        List<ModelField> list = new ArrayList<>();
        collectFields(list, this, allFields, null);

        return list;
    }

    private void collectFields(List<ModelField> list, Model model, boolean allFields, Function<ModelField, Boolean> nextFunction) {
        if (Objects.isNull(model)) {
            return;
        }
        if (!model.isPersist()) {
            return;
        }
        List<ModelField> modelFields = MODEL_CACHE_SERVICE.findModelFields(model.id(), model.versionId());

        if (Objects.nonNull(nextFunction)) {
            list.addAll(modelFields.stream().filter(nextFunction::apply).toList());
            if (!list.isEmpty()) {
                return;
            }
        } else {
            list.addAll(modelFields);
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
        return Objects.nonNull(parent()) && parent().isPersist();
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
        }).map(ModelField::ref).toList();
    }

    @Override
    public ModelGroup group() {
        if (Objects.isNull(object())) {
            return ModelGroupImpl.NULL;
        }
        if (ValueUtil.isEmpty(object().groupId())) {
            return ModelGroupImpl.NULL;
        }
        return MODEL_CACHE_SERVICE.findModelGroupById(object().groupId());
    }

    @Override
    public List<String> paths() {
        List<String> paths = new ArrayList<>();
        List<ModelGroup> list = group().pathGroups();
        for (ModelGroup modelGroup : list) {
            paths.add(modelGroup.object().name());
        }
        return paths;
    }

    @Override
    public String path(String separator) {
        StringBuilder pathBuilder = new StringBuilder();
        List<ModelGroup> list = group().pathGroups();
        for (ModelGroup modelGroup : list) {
            if (!pathBuilder.isEmpty()) {
                pathBuilder.append(separator);
            }
            pathBuilder.append(modelGroup.object().name());
        }
        return pathBuilder.toString();
    }

    @Override
    public String path() {
        return path("/");
    }
}
