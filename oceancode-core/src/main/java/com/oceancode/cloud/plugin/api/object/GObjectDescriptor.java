package com.oceancode.cloud.plugin.api.object;

import java.util.Map;

public interface GObjectDescriptor {
    <T> T create(GObject container, Class<T> typeClass, String category, Object param);

    GObject createObject(String simpleType);
}
