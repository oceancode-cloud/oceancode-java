/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.cache;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.*;

import java.util.Collections;
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
        if (ValueUtil.isEmpty(key)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "cache key is required.");
        }
        sourceKey();
    }

    public static CacheKey of(String key) {
        return new KeyParam(key, new HashMap<>(16));
    }

    public CacheKey addParam(String argKey, String argVal) {
        return putVal(argKey, argVal);
    }

    @Override
    public CacheKey addParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }


    @Override
    public CacheKey express(String express) {
        this.resultKey = express;
        return this;
    }

    @Override
    public Map<String, Object> params() {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        return this.params;
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

    public String pattern() {
        String val = commonConfig.getValue("oc.cache." + key + ".key-pattern");
        if (ValueUtil.isEmpty(val)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key-pattern is required.key:" + key);
        }
        return val;
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
        Long val = Long.parseLong(commonConfig.getValue("oc.cache." + key + ".expire", "3600000"));
        return originalValue ? val : val + CacheUtil.randomExpire(key);
    }


    public boolean hasExpire(String keyId) {
        return CacheUtil.isHotKey(keyId) && expire(true) == -1;
    }

    public boolean enabledProjectId() {
        return Boolean.parseBoolean(commonConfig.getValue("oc.cache." + key + ".project-id.enabled", "false"));
    }

    public boolean enabledTenantId() {
        return Boolean.parseBoolean(commonConfig.getValue("oc.cache." + key + ".tenant-id.enabled", "false"));
    }

    public boolean enabledUserId() {
        return Boolean.parseBoolean(commonConfig.getValue("oc.cache." + key + ".user-id.enabled", "false"));
    }

    public String sourceKey() {
        if (Objects.nonNull(this.sourceKey)) {
            return this.sourceKey;
        }
        this.sourceKey = commonConfig.getValue("oc.cache." + key + ".source.id");
        return sourceKey;
    }

    @Override
    public String key() {
        return this.key;
    }
}
