package com.oceancode.cloud.api;

public interface IdGenerator {
    Object nextId();

    Class<?> getType();
}
