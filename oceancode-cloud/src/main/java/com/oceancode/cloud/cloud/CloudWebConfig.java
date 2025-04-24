package com.oceancode.cloud.cloud;

import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.common.bean.BeanCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CloudWebConfig {
    @Bean(name = ApiClient.SERVICE_CLIENT_NAME)
    @LoadBalanced
    @Conditional(BeanCondition.class)
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
