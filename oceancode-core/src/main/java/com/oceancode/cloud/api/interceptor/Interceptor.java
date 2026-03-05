package com.oceancode.cloud.api.interceptor;

public interface Interceptor {
    void before(String resourceId, int resourceType);

    void after(String resourceId, int resourceType);
}
