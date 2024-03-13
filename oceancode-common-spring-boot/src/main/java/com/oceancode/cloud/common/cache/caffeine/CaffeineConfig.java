/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
@ConditionalOnClass({Caffeine.class})
public class CaffeineConfig {
    @Bean("caffeineDefaultCache")
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .expireAfter(new CaffeineExpiry())
                .initialCapacity(100)
                .maximumSize(10000)
                .build();
    }

    public static class CaffeineExpiry implements Expiry<String, Object> {
        @Override
        public long expireAfterCreate(@NonNull String key, @NonNull Object value, long currentTime) {
            return 0;
        }

        @Override
        public long expireAfterUpdate(@NonNull String key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(@NonNull String key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
            return currentDuration;
        }
    }
}
