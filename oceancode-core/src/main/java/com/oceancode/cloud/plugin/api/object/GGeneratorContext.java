package com.oceancode.cloud.plugin.api.object;

public interface GGeneratorContext extends GContext {
    String getFieldType(GObject field, boolean isList);
}
