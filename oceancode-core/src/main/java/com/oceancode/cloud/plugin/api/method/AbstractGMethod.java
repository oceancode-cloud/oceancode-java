package com.oceancode.cloud.plugin.api.method;

import com.oceancode.cloud.plugin.api.AbstractGObject;

public abstract class AbstractGMethod<T> extends AbstractGObject<T> implements GMethod<T> {
    public AbstractGMethod(T object) {
        super(object);
    }
}
