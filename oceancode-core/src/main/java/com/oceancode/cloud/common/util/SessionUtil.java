/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

/**
 * @author qinjiawang
 */
public final class SessionUtil {
    private final static ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private final static ThreadLocal<Long> PROJECT_ID = new ThreadLocal<>();
    private final static ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

    private SessionUtil() {
    }

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

    public static Long projectId(boolean mustNotEmpty) {
        Long value = projectId();
        if (value == null && mustNotEmpty) {
            throw new BusinessRuntimeException(CommonErrorCode.PROJECT_ID_MISSING, "projectId is required.");
        }
        return value;
    }

    public static Long userId(boolean mustNotEmpty) {
        Long value = userId();
        if (value == null && mustNotEmpty) {
            throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
        }
        return value;
    }

    public static Long tenantId(boolean mustNotEmpty) {
        Long value = tenantId();
        if (value == null && mustNotEmpty) {
            throw new BusinessRuntimeException(CommonErrorCode.TENANT_ID_MISSING, "projectId is required.");
        }
        return value;
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
