package com.oceancode.cloud.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.oceancode.cloud.api.LockActionCallback;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.LockService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnClass({Caffeine.class})
public class CaffeineLockServiceImpl implements LockService {

    @Resource
    private LocalCacheService localCacheService;

    @Override
    public void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback) {
        localCacheService.tryLockWith(cacheKey,timeout, callback);
    }
}
