package com.oceancode.cloud.api.cache;

public interface CacheKey {
    Long expire();

    String parseKey();

    String parseBKey();

    String pattern();

    String sourceKey();

    String key();

    CacheKey addParam(String argKey, String argVal);

    CacheKey addParam(String argKey, Long argVal);

    CacheKey addParam(String argKey, Integer argVal);
}
