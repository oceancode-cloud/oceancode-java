package com.oceancode.cloud.plugin.api.object;

import com.oceancode.cloud.plugin.api.object.model.GModelField;

public interface GGeneratorContext extends GContext {
    String getFieldType(GModelField<?> field, boolean isList);
}
