package com.oceancode.cloud.plugin.api.model;

import com.oceancode.cloud.plugin.api.AbstractGObject;

public abstract class AbstractGModel<T> extends AbstractGObject<T> implements GModel<T> {
    public AbstractGModel(T object) {
        super(object);
    }
}
