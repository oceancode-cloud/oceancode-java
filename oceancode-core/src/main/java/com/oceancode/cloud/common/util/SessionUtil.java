/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

/**
 * @author qinjiawang
 */
public final class SessionUtil {
    private final static ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private final static ThreadLocal<Long> PROJECT_ID = new ThreadLocal<>();
    private final static ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static void setProjectId(Long projectId) {
        PROJECT_ID.set(projectId);
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long userId() {
        return USER_ID.get();
    }

    public static Long projectId() {
        return PROJECT_ID.get();
    }

    public static Long tenantId() {
        return TENANT_ID.get();
    }

    public static void remove() {
        USER_ID.remove();
        PROJECT_ID.remove();
        TENANT_ID.remove();
    }
}
