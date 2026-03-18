package com.oceancode.cloud.plugin.api.object;

public class GTypeObject<T> extends GObject {
    private T object;

    public GTypeObject(T object) {
        this.object = object;
    }

    public T object() {
        return object;
    }
}
