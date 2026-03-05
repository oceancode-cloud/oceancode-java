package com.oceancode.cloud.api.autoconfig;

public interface AutoConfigDescriptor {
    default Class<?> getInfoTypeClass() {
        return null;
    }
}
