package com.oceancode.cloud.api;

import java.util.Collections;
import java.util.Set;

public interface MFieldObject {
    String id();

    String modelId();

    String field();

    String refModelId();

    String versionId();

    String type();

    default Set<String> tags() {
        return Collections.emptySet();
    }
}
