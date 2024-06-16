package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.common.config.CommonConfig;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityResourceIntercepator implements HandlerInterceptor {
    private CommonConfig commonConfig;

    public SecurityResourceIntercepator(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
}
