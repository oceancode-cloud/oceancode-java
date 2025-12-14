package com.oceancode.cloud.function;

public interface SimplePlugin extends Plugin {
    @Override
    default boolean isFunction() {
        return false;
    }
}
