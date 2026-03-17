package com.oceancode.cloud.plugin.api;

@FunctionalInterface
public interface GObjectCreateFunction<T> {
    GObject<T> create(GContext context, T object);
}
