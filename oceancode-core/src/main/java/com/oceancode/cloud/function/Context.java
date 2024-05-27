package com.oceancode.cloud.function;

public interface Context {

    boolean hasSelectionFields(String field,String... fields);
}
