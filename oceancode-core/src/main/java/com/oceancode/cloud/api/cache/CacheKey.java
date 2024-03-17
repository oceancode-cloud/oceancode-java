package com.oceancode.cloud.api.cache;

import java.util.Map;

public interface CacheKey {
    Long expire();

    String parseKey();

    String parseBKey();

    String pattern();

    String sourceKey();


    String key();

    CacheKey addParam(String argKey, String argVal);
    CacheKey addParamNotEmpty(String argKey, String argVal);
    CacheKey addParamNotEmpty(String argKey, Long argVal);

    CacheKey addParams(Map<String, Object> params);

    CacheKey next();

    Map<String, Object> params();

    CacheKey addParam(String argKey, Long argVal);

    CacheKey addParam(String argKey, Integer argVal);

    CacheKey express(String express);
}
