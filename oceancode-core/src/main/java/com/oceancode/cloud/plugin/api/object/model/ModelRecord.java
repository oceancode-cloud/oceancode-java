package com.oceancode.cloud.plugin.api.object.model;

public interface ModelRecord {
    void record(ModifyType type, String source, Object newValue, Object oldValue);
}
