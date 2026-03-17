package com.oceancode.cloud.plugin.api.model;

public abstract class AbstractGModelField<T> extends AbstractGModel<T> implements GModelField<T> {
    public AbstractGModelField(T object) {
        super(object);
    }
}
