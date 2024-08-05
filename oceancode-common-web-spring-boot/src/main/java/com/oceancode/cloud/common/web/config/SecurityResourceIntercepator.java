package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.common.config.CommonConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


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
