package com.oceancode.cloud.plugin.api;

import java.util.List;

public interface GContext {
    void init();

    <T extends GObject<E>, E> List<T> findAll(Class<T> objectClass, Class<E> typeClass);

    <T extends GObject<E>, E> T findById(Class<T> objectClass, Class<E> typeClass, String id);
}
