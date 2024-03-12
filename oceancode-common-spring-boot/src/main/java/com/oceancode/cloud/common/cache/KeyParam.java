/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.cache;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class KeyParam implements CacheKey {
    private Map<String, Object> params;
    private String key;
    private String sourceKey;
    private boolean isExpress;
    private static CommonConfig commonConfig;

    private String resultKey;

    private KeyParam(String key, Map<String, Object> params) {
        this.params = params;
        this.key = key;
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
        this.isExpress = true;
    }

    public static CacheKey of(String key) {
        return new KeyParam(key, new HashMap<>(16));
    }

    public static CacheKey of(String key, String argKey, String argVal) {
        return of(key).addParam(argKey, argVal);
    }

    public static CacheKey of(String key, String argKey, Long argVal) {
        return of(key).addParam(argKey, argVal);
    }

    public static CacheKey of(String key, String argKey, Integer argVal) {
        return of(key).addParam(argKey, argVal);
    }


    public CacheKey addParam(String argKey, String argVal) {
        return putVal(argKey, argVal);
    }

    public CacheKey addParam(String argKey, Long argVal) {
        return putVal(argKey, argVal);
    }

    public CacheKey addParam(String argKey, Integer argVal) {
        return putVal(argKey, argVal);
    }

    private CacheKey putVal(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public CacheKey sourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
        return this;
    }

    public String pattern() {
        return commonConfig.getValue("app.cache." + key + ".key-pattern");
    }

    public String parseKey() {
        if (!isExpress) {
            checkKey(key);
            return wrapperKey(key);
        }
        if (Objects.nonNull(resultKey)) {
            return resultKey;
        }
        String keyPattern = pattern();
        if (ValueUtil.isEmpty(keyPattern)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key %s not found", this.key);
        }
        resultKey = wrapperKey(ExpressUtil.parse(keyPattern, params, String.class));
        checkKey(resultKey);
        return resultKey;
    }

    private void checkKey(String key) {
        if (key.length() > 128) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "the maximum length of the key is 64,current:" + key.length());
        }
    }

    private String wrapperKey(String key) {
        String resultKey = "";
        if (enabledUserId()) {
            resultKey = "_user:id:" + SessionUtil.userId() + ":";
        }
        if (enabledProjectId()) {
            resultKey = "_project:id:" + SessionUtil.projectId() + ":";
        }
        if (enabledTenantId()) {
            resultKey = "_tenant:id:" + SessionUtil.tenantId() + ":";
        }
        return resultKey + key;
    }

    public String parseBKey() {
        return parseKey() + ":_b";
    }

    public Long expire() {
        return expire(false);
    }

    public Long expire(boolean originalValue) {
        Long val = Long.parseLong(commonConfig.getValue("app.cache." + key + ".expire", "3600000"));
        return originalValue ? val : val + CacheUtil.randomExpire(key);
    }


    public boolean hasExpire(String keyId) {
        return CacheUtil.isHotKey(keyId) && expire(true) == -1;
    }

    public boolean enabledProjectId() {
        return Boolean.parseBoolean(commonConfig.getValue("app.cache." + key + ".project-id.enabled", "false"));
    }

    public boolean enabledTenantId() {
        return Boolean.parseBoolean(commonConfig.getValue("app.cache." + key + ".tenant-id.enabled", "false"));
    }

    public boolean enabledUserId() {
        return Boolean.parseBoolean(commonConfig.getValue("app.cache." + key + ".user-id.enabled", "false"));
    }

    public KeyParam onlyKey() {
        this.isExpress = false;
        return this;
    }

    public String sourceKey() {
        return sourceKey;
    }

    @Override
    public String key() {
        return this.key;
    }
}
