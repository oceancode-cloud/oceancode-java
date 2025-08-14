package com.oceancode.cloud.api;

public interface MObject {
    String id();

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
