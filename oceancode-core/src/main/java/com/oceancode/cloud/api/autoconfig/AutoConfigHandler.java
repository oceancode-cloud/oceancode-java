package com.oceancode.cloud.api.autoconfig;

public interface AutoConfigHandler {
    default boolean support(AutoConfigRequestContext context, AutoConfigValue value) {
        if (value != null) {
            return value.hasChangeValue();
        }
        return false;
    }

    void apply(AutoConfigRequestContext context, AutoConfigValue value);

    String getGroup();

    default String getProperty() {
        return "";
    }
}
