package com.oceancode.cloud.api.cache;

import com.oceancode.cloud.api.LockActionCallback;

public interface LockService {
    /**
     * 枷锁
     *
     * @param cacheKey 缓存Key
     * @param timeout  超时时间 单位毫秒
     * @param callback
     */
    void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback);
}
