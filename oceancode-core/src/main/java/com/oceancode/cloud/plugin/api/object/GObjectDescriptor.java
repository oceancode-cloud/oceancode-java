package com.oceancode.cloud.plugin.api.object;

public interface GObjectDescriptor {
    <T> T create(GObject container, Class<T> typeClass, String category, Object param);

    GObject createObject(String simpleType, Class<?> oClass);
}
