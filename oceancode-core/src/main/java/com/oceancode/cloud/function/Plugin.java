package com.oceancode.cloud.function;

public interface Plugin {
    default String pluginId() {
        return null;
    }
}
