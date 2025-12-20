package com.oceancode.cloud.common.service;

import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiClient {
    private String id;
    private String url;
    private WebClient.Builder clientBuilder;
    private String authorization;
    private Map<String, String> header = new HashMap<>();
    private long maxTimeout = 5 * 60 * 1000;

    protected ApiClient(String id) {
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
        if ((url.startsWith("http:/") || url.startsWith("https://")) && !headerMap().containsKey("Referer") && !headerMap().containsKey("referer")) {
            addHeader("Referer", url);
        }
        return this;
    }

    public ApiClient setBusinessParam(String key, String value) {
        addHeader(key, value);
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

    public ApiClient timeout(long timeout) {
        this.maxTimeout = timeout;
        return this;
    }

    public ApiClient authorization(String authorization) {
        this.authorization = authorization;
        if (authorization.contains(" JSESSIONID=")) {
            addHeader("Cookie", authorization);
        } else {
            addHeader("Authorization", "Bearer " + authorization);
        }
        return this;
    }

    public ApiClient authorization(String key, String authorization) {
        this.authorization = authorization;
        addHeader(key, authorization);
        return this;
    }

    protected void addHeader(String key, String value) {
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
        filleHeader(spec);
        WebClient.RequestBodySpec uri = spec.uri(url);
        if (!(data instanceof MultiValueMap)) {
            uri.contentType(MediaType.APPLICATION_JSON);
        }
        if (Objects.nonNull(data)) {
            uri.bodyValue(data);
        }
        filleHeader(spec);
        ResponseEntity<Map> responseEntity = uri
                .retrieve().toEntity(Map.class)
                .block(Duration.ofMillis(getMaxTimeout()));

        return processResult(responseEntity, returnType);
    }


    private void filleHeader(WebClient.RequestHeadersUriSpec spec) {
        for (Map.Entry<String, String> entry : headerMap().entrySet()) {
            spec.header(entry.getKey(), entry.getValue());
        }
    }

    public <T> ResultData<T> post(Object data, Class<T> returnType) {
        return addData(data, returnType);
    }

    protected <T> boolean parseResult(ResultData<T> resultData, Object data, Class<T> returnType) {
        return false;
    }

    private <T> ResultData<T> processResult(ResponseEntity<Map> responseEntity, Class<T> returnType) {
        ResultData<T> resultData = ResultData.isOk();
        if (responseEntity.getStatusCode().isError()) {
            resultData.setStatusCode(responseEntity.getStatusCode().value());
            resultData.setCode(responseEntity.getStatusCode().value() + "");
        }
        Map body = responseEntity.getBody();
        if (parseResult(resultData, body, returnType)) {
            return resultData;
        }
        Object code = null;
        Object data = null;
        Object message = null;
        boolean isResData = true;
        if (Objects.isNull(body)) {
            return resultData;
        }
        if (body.containsKey("code") || body.containsKey("status")) {
            if (body.containsKey("data")) {
                data = body.get("data");
                isResData = false;
            } else if (body.containsKey("results")) {
                data = body.get("results");
                isResData = false;
            }
            code = body.containsKey("status") ? body.get("status") : body.get("code");
            message = body.containsKey("message") ? body.get("message") : body.get("msg");
        }
        if (Objects.nonNull(code)) {
            isResData = false;
            resultData.setCode(String.valueOf(code));
        }
        if (Objects.nonNull(message)) {
            isResData = false;
            resultData.setMessage(String.valueOf(message));
        }
        if (isResData) {
            data = body;
        }
        if (Objects.isNull(data)) {
            if (Map.class.isAssignableFrom(returnType)) {
                resultData.data((T) body);
                return resultData;
            }
        }
        T targetData = null;
        if (data instanceof Map<?, ?> map) {
            targetData = JsonUtil.mapToBean(map, returnType);
            resultData.data(targetData);
        } else if (data instanceof List<?> list) {
            resultData.setResultList(list.stream().map(it -> JsonUtil.mapToBean((Map) it, returnType)).toList());
        }

        return resultData;
    }

    public <T> ResultData<T> getData(Class<T> returnType) {
        String url = getUrl();
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = clientBuilder().build().get();
        filleHeader(requestHeadersUriSpec);
        WebClient.RequestHeadersSpec<?> uri = requestHeadersUriSpec
                .uri(url);
        ResponseEntity<Map> responseEntity = uri
                .retrieve().toEntity(Map.class)
                .block(Duration.ofMillis(getMaxTimeout()));
        return processResult(responseEntity, returnType);
    }

    public <T> ResultData<T> updateData(Object data, Class<T> returnType) {
        WebClient.RequestBodyUriSpec put = clientBuilder().build().put();
        filleHeader(put);
        ResponseEntity<Map> responseEntity = put
                .uri(getUrl())
                .bodyValue(data)
                .retrieve().toEntity(Map.class)
                .block(Duration.ofMillis(getMaxTimeout()));
        return processResult(responseEntity, returnType);
    }

    public <T> ResultData<T> deleteData(Class<T> returnType) {
        String url = getUrl();
        WebClient.RequestHeadersUriSpec<?> delete = clientBuilder().build().delete();
        filleHeader(delete);
        ResponseEntity<Map> responseEntity = delete
                .uri(url)
                .retrieve().toEntity(Map.class)
                .block(Duration.ofMillis(getMaxTimeout()));
        return processResult(responseEntity, returnType);
    }
}
