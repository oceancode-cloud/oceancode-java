package com.oceancode.cloud.common.plugin;

import com.oceancode.cloud.plugin.api.GContext;
import com.oceancode.cloud.plugin.api.GObject;
import com.oceancode.cloud.plugin.api.GObjectCreateFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class GObjectUtil {
    private final static Map<Class<?>, GObjectCreateFunction<?>> MAP = new HashMap<>();

    private GObjectUtil() {
    }

    public static <E> GObject<E> create(E e) {
        return create(null, e);
    }

    public static <E> GObject<E> create(GContext context, E e) {
        return create(context, (Class<E>) e.getClass(), e);
    }

    public static <E> GObject<E> create(GContext context, Class<E> typeClass, E e) {
        GObjectCreateFunction<E> function = (GObjectCreateFunction<E>) MAP.get(typeClass);
        if (Objects.isNull(function)) {
            return null;
        }
        return function.create(context, e);
    }

    public static <T extends GObject<E>, E> void register(Class<E> typeClass, GObjectCreateFunction<E> function) {
        if (MAP.containsKey(typeClass)) {
            return;
        }
        MAP.put(typeClass, function);
    }
}
