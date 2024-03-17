package com.oceancode.cloud.common.cache;

import com.oceancode.cloud.api.LockActionCallback;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LockService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Component
@ConditionalOnBean(LockService.class)
public class LockServiceImpl implements LockService {
    private static ConcurrentMap<String, ReentrantLock> LOCK_POOLS;

    public LockServiceImpl() {
        LOCK_POOLS = new ConcurrentHashMap<>();
    }

    @Override
    public void tryLockWith(CacheKey cacheKey, long timeout, LockActionCallback callback) {
        final long threadId = Thread.currentThread().getId();
        String key = threadId + "";
        ReentrantLock lock = LOCK_POOLS.computeIfAbsent(key, k -> new ReentrantLock());
        try {
            lock.tryLock(timeout, TimeUnit.MILLISECONDS);
            callback.doAction();
        } catch (InterruptedException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        } finally {
            lock.unlock();
        }
    }
}
