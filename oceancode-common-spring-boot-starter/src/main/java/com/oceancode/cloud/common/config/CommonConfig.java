/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.springboot.simple.demo.core.common.constant.CommonConst;
import com.springboot.simple.demo.core.enums.AppModeType;
import com.springboot.simple.demo.core.util.TypeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Configuration
public class CommonConfig {
    @Resource
    private Environment environment;

    @Value("${app.server.port:80}")
    private Integer port;

    @Value("${server.port:8080}")
    private Integer httpsPort;

    private static List<String> stripPrefixes;

    private static List<String> resourcePrefix;

    public String getValue(String key, String defaultValue) {
        String val = environment.getProperty(key);
        if (ValueUtil.isEmpty(val)) {
            return defaultValue;
        }
        return val.trim();
    }

    public String getValue(String key, Supplier<String> supplier) {
        String val = environment.getProperty(key);
        if (ValueUtil.isEmpty(val)) {
            return supplier.get();
        }
        return val.trim();
    }

    public String getValue(String key) {
        return getValue(key, (String) null);
    }

    public Integer getValueAsInteger(String key, Integer defaultValue) {
        return TypeUtil.convertToInteger(getValue(key), defaultValue);
    }

    public Integer getValueAsInteger(String key) {
        return getValueAsInteger(key, null);
    }

    public Long getValueAsLong(String key, Long defaultValue) {
        return TypeUtil.convertToLong(getValue(key), defaultValue);
    }

    public Long getValueAsLong(String key) {
        return getValueAsLong(key, null);
    }

    public boolean isTrue(String key) {
        return isTrue(key, false);
    }

    public boolean isTrue(String key, boolean defaultValue) {
        String val = getValue(key);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(val);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean isFalse(String key) {
        return !isTrue(key);
    }

    public boolean enabledRpc() {
        return isTrue("app.grpc.enabled");
    }

    public boolean isStandalone() {
        return !isMicroService();
    }

    public AppModeType appMode() {
        return TypeEnum.from(getValue(CommonConst.Config.APP_MODE), AppModeType.class);
    }

    public boolean isMicroService() {
        return AppModeType.MICROSERVICE.equals(appMode());
    }

    public boolean isAuthWithToken() {
        return isTrue(CommonConst.Config.APP_AUTH_TOKEN_ENABLED, !isMicroService());
    }


    public String getHomeUrl() {
        if (!isStandalone()) {
            return null;
        }
        return getValue("app.web.index", "/index.html");
    }


    public List<String> getResourcePrefix() {
        if (Objects.nonNull(resourcePrefix)) {
            return resourcePrefix;
        }
        String value = getValue(CommonConst.Config.APP_RESOURCE_PREFIX);
        resourcePrefix = new ArrayList<>();
        if (ValueUtil.isNotEmpty(value)) {
            String[] urls = value.split(",");
            for (String url : urls) {
                resourcePrefix.add(url);
            }
        }

        return resourcePrefix;
    }

    public List<String> getStripPrefixes() {
        getResourcePrefix();
        if (Objects.nonNull(stripPrefixes)) {
            return stripPrefixes;
        }

        String value = getValue(CommonConst.Config.APP_API_PREFIX);
        stripPrefixes = new ArrayList<>();
        if (ValueUtil.isNotEmpty(value)) {
            String[] urls = value.split(",");
            for (String url : urls) {
                if (!value.endsWith("/")) {
                    throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, CommonConst.Config.APP_API_PREFIX + ":" + url + " must be endsWith /");
                }
                stripPrefixes.add(url);
            }
        }

        return stripPrefixes;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getHttpsPort() {
        return httpsPort;
    }

    public boolean enabledHttps() {
        return Objects.nonNull(httpsPort) && Objects.nonNull(port)
                && ValueUtil.isNotEmpty(getValue("server.ssl.key-store"))
                && isTrue("server.http2.enabled", false);
    }
}
