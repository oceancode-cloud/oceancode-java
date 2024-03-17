package com.oceancode.cloud.api.cache;

import com.oceancode.cloud.api.LockActionCallback;
import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.api.strategy.StrategyEnumAdaptor;

public interface LocalCacheService extends CacheService, StrategyEnumAdaptor {

    @Override
    default TypeEnum<?> support() {
        return CacheType.LOCAL;
    }

    void tryLockWith(CacheKey key, long timeout, LockActionCallback callback);
}
