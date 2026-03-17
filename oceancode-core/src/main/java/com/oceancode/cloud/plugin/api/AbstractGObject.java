package com.oceancode.cloud.plugin.api;

public abstract class AbstractGObject<T> implements GObject<T> {
    private T object;
    private GContext context;

    public AbstractGObject(T object) {
        this.object = object;
    }

    @Override
    public T object() {
        return this.object;
    }

    public GContext getContext() {
        return context;
    }

    public void setContext(GContext context) {
        this.context = context;
    }

    @Override
    public String id() {
        return null;
    }
}
