/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.web.service.CaffeineSessionServiceImpl;
import com.oceancode.cloud.common.web.service.RedisSessionServiceImpl;
import com.oceancode.cloud.common.web.service.WebSessionServiceImpl;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.SecurityInfo;
import io.undertow.servlet.api.TransportGuaranteeType;
import io.undertow.servlet.api.WebResourceCollection;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CommonConfig commonConfig;

//    @Bean
//
//    public SessionService sessionService() {
//        return new WebSessionServiceImpl();
//    }

    /**
     * 静态资源处理
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!SystemUtil.enableWeb()) {
            return;
        }
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "file:" + SystemUtil.htmlDir() + "/", "file:" + SystemUtil.publicDir() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(commonConfig))
                .addPathPatterns("/**").order(1);
    }


    @Bean
    @ConditionalOnExpression(value = "'${server.ssl.key-store}'.trim()!='' and '${server.http2.enabled}'=='true'")
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
