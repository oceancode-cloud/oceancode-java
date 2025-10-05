package com.oceancode.cloud.common.service;

import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.util.ComponentUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApiClient {
    private String id;
    private String url;
    private WebClient.Builder clientBuilder;
    private String authorization;
    private Map<String, String> header;

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
                .uri(getUrl() + "/ping")
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

    public ApiClient authorization(String authorization) {
        this.authorization = authorization;
        addHeader("Authorization", "Bearer " + authorization);
        return this;
    }

    private void addHeader(String key, String value) {
        if (Objects.isNull(header)) {
            header = new HashMap<>();
        }
        header.put(key, value);
    }

    private Map<String, String> headerMap() {
        if (Objects.isNull(header)) {
            return Collections.emptyMap();
        }
        return this.header;
    }

    public <T> ResultData<T> addData(Object data, Class<T> returnType) {
        String url = this.getUrl();
        WebClient.RequestBodyUriSpec post = clientBuilder().build()
                .post();
        for (Map.Entry<String, String> entry : headerMap().entrySet()) {
            post.header(entry.getKey(), entry.getValue());
        }
        post.uri(url).contentType(MediaType.APPLICATION_JSON);
        ResponseEntity<T> responseEntity = post.bodyValue(data).retrieve().toEntity(returnType)
                .block();
        ResultData<T> resultData = ResultData.isOk(responseEntity.getBody());
        if (responseEntity.getStatusCode().isError()) {
            resultData.setCode(responseEntity.getStatusCode().value() + "");
        }
        return resultData;
    }

}
