package com.oceancode.cloud.common.config;

import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.id.RedisIncrementIdGenerator;
import com.oceancode.cloud.common.mq.local.LocalConsumer;
import com.oceancode.cloud.common.mq.local.LocalProducer;
import com.oceancode.cloud.common.mq.redis.RedisConsumer;
import com.oceancode.cloud.common.mq.redis.RedisProducer;
import com.oceancode.cloud.common.security.AesCrypto;
import com.oceancode.cloud.common.security.Rsa2Crypto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AutoConfigService {

    @Bean
    @ConditionalOnMissingBean(BCryptPasswordEncoder.class)
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(Rsa2CryptoService.class)
    public Rsa2CryptoService cryptoService() {
        return new Rsa2Crypto();
    }

    @Bean
    @ConditionalOnMissingBean(AesCryptoService.class)
    public AesCrypto aesCrypto() {
        return new AesCrypto();
    }



    @Bean
    @ConditionalOnMissingBean(Producer.class)
    @ConditionalOnClass(RedisTemplate.class)
    public RedisProducer redisProducer() {
        return new RedisProducer();
    }


    @Bean
    @ConditionalOnMissingBean(Producer.class)
    @ConditionalOnBean({Consumer.class})
    public LocalProducer localProducer() {
        return new LocalProducer();
    }

    @Bean
    @ConditionalOnBean(LocalProducer.class)
    public LocalConsumer localConsumer() {
        return new LocalConsumer();
    }

    @Bean
    @ConditionalOnClass({CacheService.class, RedisTemplate.class})
    @ConditionalOnMissingBean
    @ConditionalOnBean
    public RedisIncrementIdGenerator redisIncrementIdGenerator() {
        return new RedisIncrementIdGenerator();
    }
}
