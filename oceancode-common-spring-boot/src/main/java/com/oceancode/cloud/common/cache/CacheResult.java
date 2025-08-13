package com.oceancode.cloud.common.cache;

import com.oceancode.cloud.api.Result;

public class CacheResult<T> implements Result<T> {
    public final static CacheResult EMPTY = new CacheResult(null);
    public final static CacheResult NULL = new CacheResult(null, false);
    private T data;
    private boolean success;

    public CacheResult(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public CacheResult(T data) {
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
