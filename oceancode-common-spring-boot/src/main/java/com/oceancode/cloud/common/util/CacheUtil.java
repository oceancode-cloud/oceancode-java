/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.cache.CacheType;
import com.oceancode.cloud.common.config.CommonConfig;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class CacheUtil {
    private static CommonConfig commonConfig;
    private static CacheService cacheService;

    private CacheUtil() {
    }


    public static void init() {
        if (Objects.nonNull(commonConfig)) {
            return;
        }
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
        try {
            cacheService = ComponentUtil.getStrategyBean(CacheService.class, CacheType.REDIS);
        } catch (Throwable e) {
            cacheService = ComponentUtil.getStrategyBean(CacheService.class, CacheType.LOCAL);
        }
    }

    public static CacheService getCache() {
        return cacheService;
    }

    public static boolean isEmpty(String keyId, Object value) {
        return emptyValue(keyId).equals(value);
    }

    public static boolean emptyEnabled(String keyId) {
        return Boolean.parseBoolean(commonConfig.getValue("app.cache." + keyId + ".empty.enabled", "true"));
    }

    public static String emptyValue(String keyId) {
        return commonConfig.getValue("app.cache." + keyId + ".empty.value", "nil");
    }

    public static String pattern(String keyId){
        return commonConfig.getValue("app.cache." + keyId + ".key-pattern");
    }

    public static long emptyExpire(String keyId) {
        // default 60s
        // expire+[0,bound) or expire-[0,bound)
        return Long.parseLong(commonConfig.getValue("app.cache." + keyId + ".empty.expire", "60000")) +
                randomExpire(keyId);
    }

    public static int maxLength(String keyId) {
        //B default 10KB
        return Integer.parseInt(commonConfig.getValue("app.cache." + keyId + ".max-length", "10240"));
    }

    public static int replica(String keyId) {
        return Integer.parseInt(commonConfig.getValue("app.cache." + keyId + ".replica", "1"));
    }

    public static long totalLength(String keyId) {
        return maxLength(keyId) * replica(keyId);
    }

    public static long randomExpire(String keyId) {
        // default 60s
        int value = Integer.parseInt(commonConfig.getValue("app.cache." + keyId + ".expire.random", "60000"));
        if (value == 0) {
            return 0;
        }
        if (value > 0) {
            return ThreadLocalRandom.current().nextInt(value);
        }
        return -ThreadLocalRandom.current().nextInt(-value);
    }

    public static boolean isHotKey(String keyId) {
        return Boolean.parseBoolean(commonConfig.getValue("app.cache." + keyId + ".hot.enabled", "false"));
    }


    public static boolean enabledAb(String keyId) {
        return isHotKey(keyId) &&
                Boolean.parseBoolean(commonConfig.getValue("app.cache." + keyId + ".concurrency.ab.enabled", "false"));
    }
}
