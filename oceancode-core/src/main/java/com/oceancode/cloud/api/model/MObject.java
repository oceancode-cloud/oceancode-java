package com.oceancode.cloud.api.model;

import java.util.List;

public interface MObject {
    String id();

    default String name() {
        return null;
    }

    default String groupId() {
        return null;
    }

    default String parentId() {
        return null;
    }

    default String versionId() {
        return null;
    }

    default String type() {
        return null;
    }

}
