/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.common.ApiClientImpl;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.convert.PartFileConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

    private CommonConfig commonConfig;

    @Bean
    @Primary
    @ConditionalOnMissingBean({WebClient.Builder.class})
    public WebClient.Builder webApiClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @ConditionalOnMissingBean({ApiClient.class})
    public ApiClient apiClient(ApplicationContext applicationContext) {
        return new ApiClientImpl(applicationContext);
    }

    public WebConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    //    @Bean
//    @ConditionalOnExpression(value = "'${oc.web.enable}'=='true'")
//    public CustomErrorController customErrorController() {
//        return new CustomErrorController();
//    }

//    @Bean
//    @ConditionalOnMissingBean(ResourcePermissionService.class)
//    public PermissionResourceService permissionResourceService() {
//        return new ResourcePermissionServiceImpl();
//    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PartFileConvert());
    }

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
        File htmlDir = new File(SystemUtil.htmlDir());
        if (!htmlDir.exists()) {
            htmlDir.mkdirs();
        }
        File publicDir = new File(SystemUtil.publicDir());
        if (!publicDir.exists()) {
            publicDir.mkdirs();
        }
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


//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(new EncryptHttpMessageConverter());
//    }


}
