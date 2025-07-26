package com.oceancode.cloud.api;

import java.util.function.Supplier;

public interface IncrementIdGenerator {
    default Long nextId(String namespace, Long base) {
        return nextId(namespace, base, 1);
    }

    default Long nextId(String namespace, Long base, int setup) {
        return nextId(namespace, () -> base, setup);
    }

    Long nextId(String namespace, Supplier<Long> baseFunc, int setup);

    default Long nextId(String namespace, Supplier<Long> baseFunc) {
        return nextId(namespace, baseFunc, 1);
    }


    default Long nextId() {
        return nextId("all", 1L);
    }
}
