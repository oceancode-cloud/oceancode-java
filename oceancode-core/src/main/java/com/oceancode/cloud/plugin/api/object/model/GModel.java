package com.oceancode.cloud.plugin.api.object.model;

import com.oceancode.cloud.plugin.api.object.GDescription;
import com.oceancode.cloud.plugin.api.object.GName;
import com.oceancode.cloud.plugin.api.object.GTypeObject;


public class GModel<T> extends GTypeObject<T> implements GDescription, GName {

    public GModel(T object) {
        super(object);
    }

    public String modelId() {
        return propAsString("modelId");
    }
}
