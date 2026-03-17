package com.oceancode.cloud.plugin.api;

public interface GObject<T> {
    T object();

    Long sourceId();

    String id();
}
