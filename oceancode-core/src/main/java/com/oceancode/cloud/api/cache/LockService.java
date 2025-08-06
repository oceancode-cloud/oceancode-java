package com.oceancode.cloud.api.cache;

import com.oceancode.cloud.api.LockActionCallback;

import java.util.Objects;

public interface LockService {
    /**
     * 枷锁
     *
     * @param cacheKey 缓存Key
     * @param timeout  超时时间 单位毫秒
     * @param callback
     */
    void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback);

    default void tryLockWith(CacheKey cacheKey, LockActionCallback callback) {
        Long expire = cacheKey.expire(true);
        if (Objects.isNull(expire)) {
            expire = 3000L;
        }
        if (expire > 60 * 1000L) {
            expire = 60 * 1000L;
        }
        tryLockWith(cacheKey, expire, callback);
    }
}
