package com.oceancode.cloud.api;

public interface MObject {
    String id();

    default String groupId() {
        return null;
    }

    default String parentId() {
        return null;
    }
}
