package com.oceancode.cloud.cloud;

import com.oceancode.cloud.api.ApiClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CloudWebConfig {
    @Bean(name = ApiClient.SERVICE_CLIENT_NAME)
    @LoadBalanced
    @Conditional(WebApiCondition.class)
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
