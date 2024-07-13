/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.api.mq;

import com.oceancode.cloud.common.util.SessionUtil;

import java.util.Objects;

public final class MessageUtil {
    private MessageUtil() {
    }

    public static void initMessage(Message<?> message) {
        if (Objects.isNull(message)) {
            return;
        }
        message.setUserId(SessionUtil.userId());
        try {
            message.setProjectId(SessionUtil.projectId());
        } catch (Exception e) {
            // ignore
        }
        try {
            message.setTenantId(SessionUtil.tenantId());
        } catch (Exception e) {
            //ignore
        }
    }

    public static void setSession(Message<?> message) {
        SessionUtil.setUserId(message.getUserId());
        SessionUtil.setProjectId(message.getProjectId());
        SessionUtil.setTenantId(message.getTenantId());
    }
}
