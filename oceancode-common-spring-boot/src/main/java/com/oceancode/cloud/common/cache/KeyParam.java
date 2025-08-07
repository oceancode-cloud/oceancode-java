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
    public static final String DEFAULT_KEY = "master";
    private Map<String, Object> params = new HashMap<>();
    private final String key;
    private String sourceKey;
    private boolean isExpress;
    private final Boolean isKeyPattern;
    private Long expireIn;

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
        this.isKeyPattern = true;
        sourceKey();
    }

    private KeyParam(String key, Long expireIn) {
        this.key = key;
        this.isExpress = false;
        this.isKeyPattern = false;
        this.expireIn = expireIn;
    }

    public static CacheKey of() {
        return of(DEFAULT_KEY);
    }

    public static CacheKey of(String key) {
        return of(key, true, null);
    }

    public static CacheKey of(String key, boolean isPatternKey) {
        return of(key, isPatternKey, null);
    }

    public static CacheKey of(String key, boolean isPatternKey, Long expireIn) {
        if (!isPatternKey) {
            return new KeyParam(key, expireIn);
        }
        return new KeyParam(key, new HashMap<>(16));
    }


    public CacheKey addParam(String argKey, String argVal) {
        return putVal(argKey, argVal);
    }

    @Override
    public CacheKey addParamNotEmpty(String argKey, String argVal) {
        if (ValueUtil.isEmpty(argVal)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, argKey + " is requried.");
        }
        return addParam(argKey, argVal);
    }

    @Override
    public CacheKey addParamNotEmpty(String argKey, Long argVal) {
        if (ValueUtil.isEmpty(argVal)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, argKey + " is requried.");
        }
        return addParam(argKey, argVal);
    }

    @Override
    public CacheKey addParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    @Override
    public CacheKey next() {
        return this;
    }

    @Override
    public CacheKey express(String express) {
        this.resultKey = express;
        this.isExpress = true;
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
        if (DEFAULT_KEY.equals(this.key)) {
            if (ValueUtil.isEmpty(val)) {
                val = this.key;
            }
        }
        if (ValueUtil.isEmpty(val)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key-pattern is required.key:" + key);
        }
        return val;
    }

    public String parseKey() {
        if (!isKeyPattern) {
            return resultKey;
        }
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
        Map<String, Object> tempMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : params().entrySet()) {
            Object value = entry.getValue();
            if (Objects.isNull(value)) {
                continue;
            }
            if (value instanceof String str) {
                value = str.trim();
            }
            tempMap.put(entry.getKey(), value);
        }
        tempMap.put("projectId", SessionUtil.projectId(false));
        tempMap.put("userId", SessionUtil.userId(false));
        tempMap.put("tenantId", SessionUtil.tenantId(false));
        resultKey = wrapperKey(ExpressUtil.parse(keyPattern, tempMap, String.class)).trim();
        if (resultKey.endsWith(":")) {
            resultKey = resultKey.substring(0, resultKey.lastIndexOf(":"));
        }
        if (resultKey.contains("::")) {
            resultKey = resultKey.replace("::", ":");
        }
        checkKey(resultKey);
        return resultKey;
    }

    private void checkKey(String key) {
        if (key.length() > 128) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "the maximum length of the key is 64,current:" + key.length());
        }
    }

    private String wrapperKey(String key) {
        if (!isKeyPattern) {
            return key;
        }
        String resultKey = "";
        if (enabledUserId()) {
            resultKey = "_user:" + SessionUtil.userId() + ":";
        }
        if (enabledProjectId()) {
            resultKey = "_project:" + SessionUtil.projectId() + ":";
        }
        if (enabledTenantId()) {
            resultKey = "_tenant:" + SessionUtil.tenantId() + ":";
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
        if (originalValue) {
            if (Objects.nonNull(expireIn)) {
                return expireIn;
            }
            String val = commonConfig.getValue("oc.cache." + key + ".expire");
            if (ValueUtil.isNotEmpty(val)) {
                expireIn = Long.parseLong(val);
            }
            return expireIn;
        }
        if (Objects.nonNull(expireIn)) {
            return expireIn + CacheUtil.randomExpire(key);
        }
        expireIn = 3600000L + CacheUtil.randomExpire(key);
        if (!isKeyPattern) {
            return expireIn;
        }
        long val = Long.parseLong(commonConfig.getValue("oc.cache." + key + ".expire", "3600000"));
        return originalValue ? val : val + CacheUtil.randomExpire(key);
    }


    public boolean hasExpire(String keyId) {
        if (!isKeyPattern) {
            return Objects.nonNull(expireIn);
        }
        return CacheUtil.isHotKey(keyId) && expire(true) == -1;
    }

    public boolean enabledProjectId() {
        return isKeyPattern && Boolean.parseBoolean(commonConfig.getValue("oc.cache." + key + ".project-id.enabled", "false"));
    }

    public boolean enabledTenantId() {
        return isKeyPattern && Boolean.parseBoolean(commonConfig.getValue("oc.cache." + key + ".tenant-id.enabled", "false"));
    }

    public boolean enabledUserId() {
        return isKeyPattern && Boolean.parseBoolean(commonConfig.getValue("oc.cache." + key + ".user-id.enabled", "false"));
    }

    public String sourceKey() {
        if (!isKeyPattern) {
            return key;
        }
        if (Objects.nonNull(this.sourceKey)) {
            return this.sourceKey;
        }
        this.sourceKey = commonConfig.getValue("oc.cache." + key + ".source.id", this.key);
        if (ValueUtil.isEmpty(this.sourceKey)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "oc.cache." + key + ".source.id is required.");
        }
        return sourceKey;
    }

    @Override
    public String key() {
        return this.key;
    }
}
