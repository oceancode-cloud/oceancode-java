/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author qinjiawang
 */
public final class SessionUtil {
    private final static InheritableThreadLocal<Long> USER_ID = new InheritableThreadLocal<>();
    private final static InheritableThreadLocal<Long> PROJECT_ID = new InheritableThreadLocal<>();
    private final static InheritableThreadLocal<Long> TENANT_ID = new InheritableThreadLocal<>();
    private final static InheritableThreadLocal<String> BRANCH = new InheritableThreadLocal<>();
    private final static InheritableThreadLocal<String> CLIENT_ID = new InheritableThreadLocal<>();
    private final static InheritableThreadLocal<String> REQUEST_ID = new InheritableThreadLocal<>();
    private final static InheritableThreadLocal<String> CURSOR = new InheritableThreadLocal<>();

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

    public static void setBranch(String branch) {
        BRANCH.set(branch);
    }

    public static void setClientId(String clientId) {
        CLIENT_ID.set(clientId);
    }

    public static Long userId() {
        return USER_ID.get();
    }

    public static Long projectId() {
        return PROJECT_ID.get();
    }

    public static String requestId() {
        return REQUEST_ID.get();
    }

    public static void setRequestId(String id) {
        if (ValueUtil.isNotEmpty(requestId())) {
            if (ValueUtil.isEmpty(id)) {
                return;
            }
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "request already initial");
        }
        REQUEST_ID.set(id);
    }

    public static void removeRequestId() {
        REQUEST_ID.remove();
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

    public static String clientId() {
        return CLIENT_ID.get();
    }

    public static String clientId(boolean mustNotEmpty) {
        String clientId = clientId();
        if (mustNotEmpty && ValueUtil.isEmpty(clientId)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "client id is required.");
        }
        return clientId;
    }

    public static Long tenantId(boolean mustNotEmpty) {
        Long value = tenantId();
        if (value == null && mustNotEmpty) {
            throw new BusinessRuntimeException(CommonErrorCode.TENANT_ID_MISSING, "projectId is required.");
        }
        return value;
    }

    public static String branch(boolean mustNotEmpty) {
        String value = branch();
        if (value == null || value.isEmpty()) {
            throw new BusinessRuntimeException(CommonErrorCode.TENANT_ID_MISSING, "branch is required.");
        }
        return value;
    }

    public static Long tenantId() {
        return TENANT_ID.get();
    }

    public static String branch() {
        return BRANCH.get();
    }

    public static String cursor() {
        return CURSOR.get();
    }

    public static void setCursor(String id) {
        if (ValueUtil.isNotEmpty(CURSOR.get())) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "cursor can't be override.");
        }
        CURSOR.set(id);
    }

    public static void removeCursor() {
        CURSOR.remove();
    }

    public static void remove() {
        USER_ID.remove();
        PROJECT_ID.remove();
        TENANT_ID.remove();
        BRANCH.remove();
        CLIENT_ID.remove();
        REQUEST_ID.remove();
        CURSOR.remove();
    }

    public static List<Object> getValues() {
        return Collections.unmodifiableList(Arrays.asList(tenantId(), projectId(), userId(), branch(), clientId(), requestId(), cursor()));
    }

    public static void setValues(List<Object> values) {
        if (ValueUtil.isEmpty(values) || values.size() < 7) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "invalid");
        }
        setTenantId((Long) values.get(0));
        setProjectId((Long) values.get(1));
        setUserId((Long) values.get(2));
        setBranch((String) values.get(3));
        setClientId((String) values.get(4));
        String requestId = (String) values.get(5);
        if (!Objects.equals(requestId(), requestId)) {
            setRequestId(requestId);
        }

        String cursor = (String) values.get(6);
        if (!Objects.equals(cursor(), cursor)) {
            setCursor(cursor);
        }
    }
}
