package com.oceancode.cloud.model;

import com.oceancode.cloud.api.model.MGroupObject;

import java.util.List;

public interface ModelGroup extends BaseObject<MGroupObject> {
    ModelGroup parent();

    List<ModelGroup> parents();

    List<ModelGroup> pathGroups();
}
