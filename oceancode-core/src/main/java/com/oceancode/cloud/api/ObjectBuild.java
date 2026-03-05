package com.oceancode.cloud.api;

public interface ObjectBuild<T> {
    T build();

    T build(T data);
}
