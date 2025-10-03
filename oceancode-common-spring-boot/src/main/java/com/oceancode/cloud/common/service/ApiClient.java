package com.oceancode.cloud.common.service;

import com.oceancode.cloud.common.util.ComponentUtil;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

public class ApiClient {
    private String id;
    private String url;
    private WebClient.Builder clientBuilder;

    private ApiClient(String id) {
        this.id = id;
        clientBuilder = ComponentUtil.getBean(WebClient.Builder.class, false);

    }

    private WebClient.Builder clientBuilder() {
        return clientBuilder;
    }

    public static ApiClient of(String id) {
        return new ApiClient(id);
    }

    public ApiClient url(String url) {
        this.url = url;
        return this;
    }

    public boolean ping() {
        String text = clientBuilder()
                .build()
                .get()
                .uri(getUrl()+"/ping")
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofMillis(getMaxTimeout()));

        return "pong".equalsIgnoreCase(text);
    }

    private String getUrl() {
        return this.url;
    }

    private long getMaxTimeout() {
        return 5 * 60 * 1000;
    }

}
