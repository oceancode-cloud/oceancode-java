package com.oceancode.platform;

import com.oceancode.cloud.common.fetcher.ApiDataFetcher;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CloudWebConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ApiDataFetcher apiDataFetcher(){
        return new ApiDataFetcher();
    }
}
