package com.oceancode.cloud.common.cache.caffeine;

import com.oceancode.cloud.api.LockActionCallback;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.LockService;
import jakarta.annotation.Resource;

public class CaffeineLockServiceImpl implements LockService {

    @Resource
    private LocalCacheService localCacheService;

    @Override
    public void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback) {
        localCacheService.tryLockWith(cacheKey,timeout, callback);
    }
}
