package com.oceancode.cloud.plugin.api.model;

import com.oceancode.cloud.plugin.api.AbstractGObject;

public abstract class AbstractGModelGroup<T> extends AbstractGObject<T> implements GModelGroup<T> {
    public AbstractGModelGroup(T object) {
        super(object);
    }
}
