/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private CommonConfig commonConfig;
    private SessionService sessionService;
    private static final List<String> UNAUTHENTICATED_URLS =
            Arrays.asList(
                    "/ping", "/favicon.ico","/error"
            );

    public AuthenticationInterceptor(CommonConfig commonConfig, SessionService sessionService) {
        this.commonConfig = commonConfig;
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI();
        if (requestUrl.equals("/")) {
            response.sendRedirect(commonConfig.getHomeUrl());
            return false;
        }
        String prefix = requestUrl;
        if (requestUrl.startsWith("/")) {
            if (prefix.startsWith("/")) {
                prefix = prefix.substring(1);
            }
            if (prefix.contains("/")) {
                prefix = "/" + prefix.substring(0, prefix.indexOf("/") + 1);
            }
        }

        List<String> prefixUrls = commonConfig.getStripPrefixes();
        if (prefixUrls.contains(prefix) && !requestUrl.startsWith("/api/")) {
            int pos = prefixUrls.indexOf(prefix);
            if (pos >= 0) {
                String apiPrefix = prefixUrls.get(pos);
                String targetUrl = requestUrl.substring(apiPrefix.length() - 1);
                request.getRequestDispatcher(targetUrl).forward(request, response);
                return true;
            }
        } else if (isStaticResource(prefix)) {
            return true;
        }
        ApiUtil.processCommonArguments(request);
        if (UNAUTHENTICATED_URLS.contains(requestUrl)) {
            return true;
        }
        if (!commonConfig.isStandalone() && !commonConfig.isAuthWithToken()) {
            String userId = request.getHeader("x-user-id");
            if (ValueUtil.isEmpty(userId)) {
                userId = request.getParameter("x-user-id");
            }
            if (ValueUtil.isEmpty(userId)) {
                throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
            }
            SessionUtil.setUserId(Long.parseLong(userId));
            return true;
        }
        boolean ret = sessionService.isLogin();
        if (ret) {
            return true;
        }

        throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
    }

    private boolean isStaticResource(String prefix) {
        return commonConfig.getResourcePrefix().contains(prefix);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SessionUtil.remove();
    }
}
