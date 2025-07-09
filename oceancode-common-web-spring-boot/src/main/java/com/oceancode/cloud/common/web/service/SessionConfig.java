package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class SessionConfig {

    @Bean
    @ConditionalOnMissingBean(SessionService.class)
    @ConditionalOnBean({RedisCacheService.class, RedisTemplate.class})
    @ConditionalOnExpression(value = "'${oc.session.cache.type}'=='redis'")
    public RedisSessionServiceImpl redisSessionService() {
        return new RedisSessionServiceImpl();
    }

    @Bean
    @ConditionalOnBean({LocalCacheService.class})
    @ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
    @ConditionalOnProperty(name = "oc.session.cache.type",havingValue = "caffeine", matchIfMissing = true)
    public CaffeineSessionServiceImpl caffeineSessionService() {
        return new CaffeineSessionServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(SessionService.class)
    public WebSessionServiceImpl webSessionService() {
        return new WebSessionServiceImpl();
    }
}
