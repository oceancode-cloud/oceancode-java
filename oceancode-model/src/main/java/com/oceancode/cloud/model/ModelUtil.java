package com.oceancode.cloud.model;

import java.util.HashMap;
import java.util.Map;

public class ModelUtil {
    private final static Map<String, Class<?>> CLASS_MAPPING = new HashMap<>();

    private ModelUtil() {
    }

    public static void registerClass(Class<?> clazz) {
        CLASS_MAPPING.put(clazz.getName(), clazz);
    }

    public static Class<?> getClass(String className) {
        return CLASS_MAPPING.get(className);
    }
}
