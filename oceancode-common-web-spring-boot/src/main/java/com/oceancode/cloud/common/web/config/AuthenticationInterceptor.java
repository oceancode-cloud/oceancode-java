/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private CommonConfig commonConfig;

    public AuthenticationInterceptor(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI();
        if (requestUrl.startsWith(CommonConst.API_PREFIX)) {
            MDC.put(CommonConst.TRACE_ID, UUID.randomUUID().toString());
            MDC.put(CommonConst.SERVICE_NAME, commonConfig.getServiceName());
            MDC.put(CommonConst.INSTANCE_NAME, commonConfig.getInstanceName());
            return doApiHandler(request);
        }
        return true;
    }

    private boolean doApiHandler(HttpServletRequest request) {
        ApiUtil.processCommonArguments(request);
        if (commonConfig.isMicroService()) {
            String userId = request.getHeader(CommonConst.X_USER_ID);
            if (ValueUtil.isEmpty(userId)) {
                userId = request.getParameter(CommonConst.X_USER_ID);
            }
            if (ValueUtil.isNotEmpty(userId)) {
                SessionUtil.setUserId(Long.parseLong(userId));
                MDC.put(CommonConst.USER_ID, userId);
            }

            String requestId = request.getHeader(CommonConst.X_REQUEST_ID);
            if (ValueUtil.isEmpty(requestId)) {
                requestId = request.getParameter(CommonConst.X_REQUEST_ID);
            }
            if (ValueUtil.isNotEmpty(requestId)) {
                MDC.put(CommonConst.REQUEST_ID, requestId);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionUtil.remove();
        MDC.clear();
    }
}
