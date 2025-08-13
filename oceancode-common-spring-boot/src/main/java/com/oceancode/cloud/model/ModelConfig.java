package com.oceancode.cloud.model;

import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.model.impl.CacheModelServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(ModelService.class)
public class ModelConfig {

    @Bean
    @ConditionalOnBean({CacheService.class})
    @ConditionalOnMissingBean(ModelService.class)
    public ModelService modelService() {
        return new CacheModelServiceImpl();
    }
}
