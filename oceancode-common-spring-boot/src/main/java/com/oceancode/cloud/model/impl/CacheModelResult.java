package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.Result;

public class CacheModelResult<T> implements Result<T> {
    private transient T data;
    private boolean success;
    public static final CacheModelResult EMPTY = new CacheModelResult(null, true);
    public static final CacheModelResult NULL = new CacheModelResult(null, false);

    public CacheModelResult(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public CacheModelResult(T data) {
        this(data, true);
    }

    @Override
    public T getResults() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getCode() {
        return "";
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
