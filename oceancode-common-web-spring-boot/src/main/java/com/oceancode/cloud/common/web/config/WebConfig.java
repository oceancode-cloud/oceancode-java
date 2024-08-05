/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.service.CaffeineSessionServiceImpl;
import com.oceancode.cloud.common.web.service.RedisSessionServiceImpl;
import com.oceancode.cloud.common.web.service.WebSessionServiceImpl;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    @Resource
    private CommonConfig commonConfig;

//    @Bean
//    @ConditionalOnExpression(value = "'${oc.web.enable}'=='true'")
//    public CustomErrorController customErrorController() {
//        return new CustomErrorController();
//    }

    /**
     * 静态资源处理
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!SystemUtil.enableWeb()) {
            return;
        }
        List<String> dirs = new ArrayList<>();
        dirs.add("classpath:/static/");
        addResourceDir(dirs, SystemUtil.htmlDir());
        addResourceDir(dirs, SystemUtil.publicDir());
        addResourceDir(dirs, SystemUtil.privateResourceDir());
        LOGGER.info("html dir " + SystemUtil.htmlDir());
        LOGGER.info("public resource dir " + SystemUtil.publicDir());
        LOGGER.info("private resource dir " + SystemUtil.privateResourceDir());
        registry.addResourceHandler("/**")
                .addResourceLocations(dirs.toArray(new String[0]));
    }

    private static void addResourceDir(List<String> list, String dir) {
        if (ValueUtil.isNotEmpty(dir)) {
            if (!dir.endsWith("/")) {
                dir = dir + "/";
            }
            list.add("file:" + dir);
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if (!SystemUtil.enableWeb()) {
            return;
        }
        String homeUrl = commonConfig.getHomeUrl();
        if (ValueUtil.isEmpty(homeUrl)) {
            return;
        }
        if (!homeUrl.startsWith("/")) {
            homeUrl = "/" + homeUrl;
        }
        registry.addViewController("/").setViewName("forward:" + homeUrl);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(commonConfig))
                .addPathPatterns("/api/**").order(1);

        if (!SystemUtil.enableWeb()) {
            return;
        }

        String securityResourceDir = SystemUtil.privateResourceUrlPrefix();
        if (ValueUtil.isNotEmpty(securityResourceDir)) {
            registry.addInterceptor(new SecurityResourceIntercepator(commonConfig))
                    .addPathPatterns(securityResourceDir).order(2);
        }
    }


    @Bean
    @ConditionalOnExpression(value = "'${server.ssl.enabled}'=='true'")
    public ServletWebServerFactory undertowFactory() {
        UndertowServletWebServerFactory undertowFactory = new UndertowServletWebServerFactory();

        undertowFactory.addBuilderCustomizers((Undertow.Builder builder) -> {
            builder.addHttpListener(commonConfig.getPort(), "0.0.0.0");
            builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
        });
        undertowFactory.addDeploymentInfoCustomizers(deploymentInfo -> {
            deploymentInfo.addSecurityConstraint(new SecurityConstraint()
                            .addWebResourceCollection(new WebResourceCollection().addUrlPattern("/*"))
                            .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
                            .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
                    .setConfidentialPortManager(exchange -> commonConfig.getHttpsPort());
        });

        return undertowFactory;
    }
}
