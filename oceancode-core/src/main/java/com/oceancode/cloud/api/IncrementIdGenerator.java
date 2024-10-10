package com.oceancode.cloud.api;

public interface IncrementIdGenerator {
    Long nextId(String namespace, Long base);

    default Long nextId() {
        return nextId(null, null);
    }
}
