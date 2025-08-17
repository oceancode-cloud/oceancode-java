package com.oceancode.cloud.model;

import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.model.PersistModelService;
import com.oceancode.cloud.model.impl.DefaultCacheModelCacheServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnClass(ModelCacheService.class)
public class ModelConfig {

    @Bean
    @ConditionalOnBean({CacheService.class})
    @ConditionalOnMissingBean
    public ModelCacheService modelCacheService(List<PersistModelService> persistModelServices) {
        return new DefaultCacheModelCacheServiceImpl(persistModelServices);
    }
}
