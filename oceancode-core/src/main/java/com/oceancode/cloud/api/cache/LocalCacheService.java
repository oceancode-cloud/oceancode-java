package com.oceancode.cloud.api.cache;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.api.strategy.StrategyEnumAdaptor;

public interface LocalCacheService extends CacheService, StrategyEnumAdaptor {

    @Override
    default TypeEnum<?> support() {
        return CacheType.LOCAL;
    }
}
