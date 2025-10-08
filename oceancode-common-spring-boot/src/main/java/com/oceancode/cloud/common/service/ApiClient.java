package com.oceancode.cloud.common.service;

import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

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
        return request(data, returnType, clientBuilder().build()
                .post());
    }

    public <T> ResultData<T> upload(Map<String, Object> data, Class<T> returnType) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>(data.size());
        data.forEach(map::add);
        return request(map, returnType, clientBuilder().build()
                .post());
    }

    public <T> ResultData<T> request(Object data, Class<T> returnType, WebClient.RequestBodyUriSpec spec) {
        String url = this.getUrl();
        for (Map.Entry<String, String> entry : headerMap().entrySet()) {
            spec.header(entry.getKey(), entry.getValue());
        }
        WebClient.RequestBodySpec uri = spec.uri(url);
        if (!(data instanceof MultiValueMap)) {
            uri.contentType(MediaType.APPLICATION_JSON);
        }
        ResponseEntity<T> responseEntity = uri.bodyValue(data)
                .retrieve().toEntity(returnType)
                .block(Duration.ofMillis(getMaxTimeout()));

        return processResult(responseEntity, returnType);
    }

    public <T> ResultData<T> post(Object data, Class<T> returnType) {
        return addData(data, returnType);
    }

    private <T> ResultData<T> processResult(ResponseEntity<T> responseEntity, Class<T> returnType) {
        ResultData<T> resultData = ResultData.isOk(responseEntity.getBody());
        if (responseEntity.getStatusCode().isError()) {
            resultData.setCode(responseEntity.getStatusCode().value() + "");
        }
        return resultData;
    }

    public <T> ResultData<T> getData(Map<String, String> data, Class<T> returnType) {
        String url = getUrl();
        if (ValueUtil.isNotEmpty(data)) {
            for (Map.Entry<String, String> item : data.entrySet()) {
                url += item.getKey() + "=" + item.getValue() + "&";
            }
        }
        ResponseEntity<T> responseEntity = clientBuilder().build().get()
                .uri(url)
                .retrieve().toEntity(returnType)
                .block(Duration.ofMillis(getMaxTimeout()));
        return processResult(responseEntity, returnType);
    }

    public <T> ResultData<T> updateData(Object data, Class<T> returnType) {
        ResponseEntity<T> responseEntity = clientBuilder().build().put()
                .uri(getUrl())
                .bodyValue(data)
                .retrieve().toEntity(returnType)
                .block(Duration.ofMillis(getMaxTimeout()));
        return processResult(responseEntity, returnType);
    }

    public <T> ResultData<T> deleteData(Map<String, String> data, Class<T> returnType) {
        String url = getUrl();
        if (ValueUtil.isNotEmpty(data)) {
            for (Map.Entry<String, String> item : data.entrySet()) {
                url += item.getKey() + "=" + item.getValue() + "&";
            }
        }
        ResponseEntity<T> responseEntity = clientBuilder().build().delete()
                .uri(url)
                .retrieve().toEntity(returnType)
                .block(Duration.ofMillis(getMaxTimeout()));
        return processResult(responseEntity, returnType);
    }
}
