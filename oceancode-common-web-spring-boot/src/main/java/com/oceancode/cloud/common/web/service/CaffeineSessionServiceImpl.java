package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.cache.CacheService;

public class CaffeineSessionServiceImpl extends RedisSessionServiceImpl {
    public CaffeineSessionServiceImpl(CacheService cacheService) {
        super(cacheService);
    }
}
