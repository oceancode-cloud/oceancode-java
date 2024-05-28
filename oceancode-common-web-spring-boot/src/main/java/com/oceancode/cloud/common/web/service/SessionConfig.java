package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    @Bean
    @ConditionalOnMissingBean(SessionService.class)
    @ConditionalOnBean({RedisCacheService.class})
    public RedisSessionServiceImpl redisSessionService() {
        return new RedisSessionServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(SessionService.class)
    @ConditionalOnBean({LocalCacheService.class})
    public CaffeineSessionServiceImpl caffeineSessionService() {
        return new CaffeineSessionServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(SessionService.class)
    public WebSessionServiceImpl webSessionService() {
        return new WebSessionServiceImpl();
    }
}
