package com.oceancode.cloud.common.config;

import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.cache.LockService;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.express.ExpressExecute;
import com.oceancode.cloud.api.file.FileService;
import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.cache.LockServiceImpl;
import com.oceancode.cloud.common.cache.caffeine.CaffeineLockServiceImpl;
import com.oceancode.cloud.common.cache.redis.RedisLockServiceImpl;
import com.oceancode.cloud.common.excel.FileServiceImpl;
import com.oceancode.cloud.common.express.ExpressEnginManager;
import com.oceancode.cloud.common.id.RedisIncrementIdGenerator;
import com.oceancode.cloud.common.mq.local.LocalConsumer;
import com.oceancode.cloud.common.mq.local.LocalProducer;
import com.oceancode.cloud.common.mq.redis.RedisProducer;
import com.oceancode.cloud.common.security.AesCrypto;
import com.oceancode.cloud.common.security.Rsa2Crypto;
import com.oceancode.cloud.file.LocalFileServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

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
    @ConditionalOnBean(RedisCacheService.class)
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

    @Bean
    @ConditionalOnMissingBean(FileService.class)
    @ConditionalOnClass(Workbook.class)
    public FileService fileService() {
        return new FileServiceImpl();
    }

    @Bean
    @ConditionalOnBean(RedisCacheService.class)
    public LockService redisLock() {
        return new RedisLockServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(LockService.class)
    @ConditionalOnBean(LocalCacheService.class)
    public LockService localLock() {
        return new CaffeineLockServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(LockService.class)
    public LockService lockService() {
        return new LockServiceImpl();
    }

    @Bean
    @ConditionalOnBean(ExpressExecute.class)
    public ExpressEnginManager expressEnginManager(Set<ExpressExecute> executes) {
        return new ExpressEnginManager(executes);
    }

    @Bean
    @ConditionalOnMissingBean(FileService.class)
    public LocalFileServiceImpl localFileService() {
        return new LocalFileServiceImpl();
    }
}
