package com.oceancode.cloud.model.impl.object;

import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.ModelGroup;
import com.oceancode.cloud.api.model.MGroupObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ModelGroupImpl extends AbstractBaseObject<MGroupObject> implements ModelGroup {
    public static final ModelGroupImpl NULL = new ModelGroupImpl();

    public ModelGroupImpl(MGroupObject object) {
        super(object);
    }

    public ModelGroupImpl(Map<String, Object> dataMap) {
        super(dataMap);
    }

    public ModelGroupImpl() {

    }

    @Override
    public String id() {
        return object().id();
    }

    @Override
    public ModelGroup parent() {
        if (Objects.isNull(object())) {
            return ModelGroupImpl.NULL;
        }
        if (ValueUtil.isEmpty(object().parentId())) {
            return ModelGroupImpl.NULL;
        }
        if ("0".equals(object().parentId())) {
            return ModelGroupImpl.NULL;
        }
        return MODEL_CACHE_SERVICE.findModelGroupById(object().parentId());
    }

    @Override
    public List<ModelGroup> parents() {
        List<ModelGroup> list = new ArrayList<>();
        ModelGroup parent = parent();
        Set<String> ids = new HashSet<>();
        while (Objects.nonNull(parent) && parent.isPersist()) {
            if (ids.contains(parent.id())) {
                break;
            }
            ids.add(parent.id());
            list.add(0, parent);
            parent = MODEL_CACHE_SERVICE.findModelGroupById(parent.id());
        }
        return list;
    }

    @Override
    public List<ModelGroup> pathGroups() {
        List<ModelGroup> list = parents();
        if (isPersist()) {
            list.add(this);
        }

        return list;
    }
}
