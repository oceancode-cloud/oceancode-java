package com.oceancode.cloud.api.model;

import java.util.Collections;
import java.util.Set;

public interface MFieldObject extends MObject {
    String modelId();

    String field();

    String refModelId();

    default Set<String> tags() {
        return Collections.emptySet();
    }
}
