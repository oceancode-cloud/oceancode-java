package com.oceancode.cloud.indexer;

import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.indexer.ReferenceIndexer;
import com.oceancode.cloud.indexer.impl.CacheReferenceIndexerImpl;
import com.oceancode.cloud.indexer.impl.LocalReferenceIndexerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndexerConfig {


    @Bean
    @ConditionalOnBean(CacheService.class)
    public ReferenceIndexer referenceIndexer() {
        return new CacheReferenceIndexerImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ReferenceIndexer.class)
    public ReferenceIndexer localReferenceIndexer() {
        return new LocalReferenceIndexerImpl();
    }
}
