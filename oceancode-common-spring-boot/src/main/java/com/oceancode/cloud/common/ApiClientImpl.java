package com.oceancode.cloud.common;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.api.ClientResult;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.query.QueryMethod;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ClientResultData;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.HttpCookie;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiClientImpl implements ApiClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiClientImpl.class);
    private CommonConfig commonConfig;

    private WebClient.Builder webClientBuilder;
    private WebClient.Builder webServiceClientBuilder;
    private final static String CONFIG_PREFIX = "::";

    public ApiClientImpl() {
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
        webClientBuilder = ComponentUtil.getBean(WebClient.Builder.class);
        try {
            webServiceClientBuilder = ComponentUtil.getBean(ApiClient.SERVICE_CLIENT_NAME, WebClient.Builder.class);
        } catch (Exception e) {

        }
    }

    private String getUrl(String url) {
        if (url.startsWith("http:/") || url.startsWith("https:/")) {
            return url;
        }
        if (!url.contains(":")) {
            return url;
        }
        String value = url.substring(url.indexOf(":") + 1);
        if (value.startsWith("http:/") || value.startsWith("https:/")) {
            return value;
        }
        String configId = url.substring(0, url.indexOf(":")).trim();
        return commonConfig.getValue("oc.api." + configId + ".base-url", "") + value;
    }

    private boolean isService(String url) {
        if (url.startsWith("http:/") || url.startsWith("https:/")) {
            return false;
        }
        if (!url.contains(":")) {
            return false;
        }
        String configId = url.substring(0, url.indexOf(":"));
        String key = "oc.api." + configId + ".type";
        return "service".equals(commonConfig.getValue(key));
    }

    private WebClient.Builder client(String url) {
        if (isService(url)) {
            return webServiceClientBuilder;
        }
        return webClientBuilder;
//                .filter(cookieFilter());
    }

    private String getQuery(List<QueryMethod> methods) {
        StringBuilder queryBuilder = new StringBuilder();
        for (QueryMethod method : methods) {
            queryBuilder.append(method.getName()).append(":");
            queryBuilder.append(method.getQuery());
        }
        return "{" + queryBuilder + "}";
    }

    @Override
    public <T> ClientResult<List<T>> queryForList(String uri, QueryMethod method, Class<T> dataTypeClass) {
        return queryForList(uri, method, ResultData.class, dataTypeClass);
    }

    private String getQueryApi() {
        return commonConfig.getValue("oc.query.url");
    }

    @Override
    public <T> ClientResult<List<T>> queryForList(QueryMethod method, Class<T> dataTypeClass) {
        return queryForList(getQueryApi(), method, dataTypeClass);
    }

    @Override
    public <T> ClientResult<T> queryFor(String uri, List<QueryMethod> methods, Class<T> dataTypeClass) {
        String query = getQuery(methods);
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        WebClient webClient = client(uri)
                .build();

        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(getUrl(uri))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        processCommon(spec, uri);

        Mono<ClientResponse> result = spec.exchange();
        ClientResultData<T> resultData = new ClientResultData<>();
        try {
            T responseData = result.flatMap(response -> {
                resultData.setHeaders(response.headers().asHttpHeaders());
                processError(resultData, response);
                return response.bodyToMono(dataTypeClass);
            }).block(Duration.ofSeconds(5L));
            resultData.setResults(responseData);
        } catch (Throwable throwable) {
            LOGGER.error("call failed", throwable);
        }

        return resultData;
    }

    @Override
    public <T> ClientResult<T> queryFor(List<QueryMethod> methods, Class<T> dataTypeClass) {
        return queryFor(getQueryApi(), methods, dataTypeClass);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<List<E>> queryForList(String uri, QueryMethod method, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        return processQueryForList(uri, Arrays.asList(method), returnTypeClass, dataTypeClass);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<List<E>> queryForList(QueryMethod method, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        return queryForList(getQueryApi(), method, returnTypeClass, dataTypeClass);
    }

    public <T extends Result<E>, E> ClientResult<List<E>> processQueryForList(String uri, List<QueryMethod> method, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        String query = getQuery(method);
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        WebClient webClient = client(uri)
                .build();

        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(getUrl(uri))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        processCommon(spec, uri);

        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(List.class, dataTypeClass);
        JavaType javaType = typeFactory.constructParametricType(returnTypeClass, inner);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);

        Mono<ClientResponse> result = spec.exchange();
        ClientResultData<List<E>> resultData = new ClientResultData<>();
        try {
            Object responseData = result.flatMap(response -> {
                resultData.setHeaders(response.headers().asHttpHeaders());
                processError(resultData, response);
                return response.bodyToMono(dataTypeClass);
            }).block(Duration.ofSeconds(5L));
            if (responseData instanceof List && ((List<?>) responseData).size() == 1) {
                Result<List<E>> o = (Result<List<E>>) ((List<?>) responseData).get(0);
                resultData.setResults((List<E>) o.getResults());
                if (!resultData.isSuccess()) {
                    resultData.setCode(o.getCode());
                    resultData.setMessage(o.getMessage());
                }
            }
        } catch (Throwable throwable) {
            LOGGER.error("call failed", throwable);
        }
        return resultData;
    }


    protected List<HttpCookie> getCookies() {
        return Collections.emptyList();
    }

    private ExchangeFilterFunction cookieFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            List<HttpCookie> cookies = getCookies();
            if (cookies != null && !cookies.isEmpty()) {
                for (HttpCookie cookie : cookies) {
                    return Mono.just(ClientRequest.from(request)
                            .cookie(cookie.getName(), cookie.getValue())
                            .build());
                }
            }
            return Mono.just(request);
        });
    }

    @Override
    public <T extends Result<E>, E> ClientResult<List<E>> postForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri)
                .build();
        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(getUrl(uri))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(params);

        processCommon(spec, uri);


        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(List.class, dataTypeClass);
        JavaType javaType = typeFactory.constructParametricType(returnTypeClass, inner);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        return processResultList(spec.exchange(), null, reference);
    }

    @Override
    public <T> ClientResult<List<T>> postForList(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri)
//                .filter(cookieFilter())
                .build();
        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(getUrl(uri))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(params);

        processCommon(spec, uri);
        return processResultList(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T> ClientResult<T> postFor(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(getUrl(uri))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(params);

        processCommon(spec, uri);
        return processResult(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<T2>, T2> ClientResult<T2> postFor(String uri, Object params, Class<T> returnTypeClass, Class<T2> dataTypeClass) {
        WebClient webClient = client(uri).build();

        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType javaType = typeFactory.constructParametricType(returnTypeClass, dataTypeClass);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);

        WebClient.RequestHeadersSpec<?> spec = webClient.post()
                .uri(getUrl(uri))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(params);

        processCommon(spec, uri);
        Mono<ClientResponse> result = spec.exchange();
        return processResult(result, dataTypeClass, reference);
    }

    private void processError(ClientResultData clientResultData, ClientResponse response) {
        if (HttpStatus.OK.equals(response.statusCode())) {
            clientResultData.setCode(CommonErrorCode.SUCCESS.getCode());
            return;
        }
        clientResultData.setCode(CommonErrorCode.ERROR.getCode());
        int code = response.statusCode().value();
        if (code >= 500) {
            clientResultData.setCode(CommonErrorCode.SERVER_ERROR.getCode());
        } else if (code >= 400) {
            if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                clientResultData.setCode(CommonErrorCode.NOT_LOGIN.getCode());
            } else if (HttpStatus.FORBIDDEN.equals(response.statusCode())) {
                clientResultData.setCode(CommonErrorCode.PERMISSION_DENIED.getCode());
            }
        }
    }

    private <T2> ClientResult<List<T2>> processResultList(Mono<ClientResponse> result, Class<T2> dataTypeClass, ParameterizedTypeReference<Object> reference) {
        ClientResultData<List<T2>> resultData = new ClientResultData<>();

        try {
            Result<List<T2>> r = (Result<List<T2>>) result.flatMap(response -> {
                resultData.setHeaders(response.headers().asHttpHeaders());
                processError(resultData, response);
                if (Objects.nonNull(reference)) {
                    return response.bodyToFlux(reference).collectList();
                } else {
                    return response.bodyToFlux(dataTypeClass).collectList();
                }
            }).block(Duration.ofSeconds(5L));
            resultData.setResults(r.getResults());
            resultData.setCode(r.getCode());
            resultData.setMessage(r.getMessage());
        } catch (Exception e) {
            LOGGER.error("call failed", e);
        }
        return resultData;
    }

    private <T2> ClientResult<T2> processResult(Mono<ClientResponse> result, Class<T2> dataTypeClass, ParameterizedTypeReference<Object> reference) {
        ClientResultData<T2> resultData = new ClientResultData<>();

        try {
            Object responseData = result.flatMap(response -> {
                // 获取响应体
                Mono<Object> body = null;
                processError(resultData, response);
                if (Objects.nonNull(reference)) {
                    body = response.bodyToMono(reference);
                } else {
                    body = response.bodyToMono(dataTypeClass);
                }

                resultData.setHeaders(response.headers().asHttpHeaders());
                return body;
            }).block(Duration.ofSeconds(5L));
            if (responseData instanceof Result) {
                Result<T2> r = (Result<T2>) responseData;
                resultData.setResults(r.getResults());
                resultData.setCode(r.getCode());
                resultData.setMessage(r.getMessage());
            } else {
                resultData.setResults((T2) responseData);
            }
        } catch (Exception e) {
            LOGGER.error("call failed", e);
        }
        return resultData;
    }

    @Override
    public <T> ClientResult<List<T>> getForList(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(getUrl(uri));
        processCommon(spec, uri);
        return processResultList(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<List<E>> getForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(getUrl(uri));
        processCommon(spec, uri);
        return processResultList(spec.exchange(), null, getResultType(returnTypeClass, dataTypeClass));
    }

    @Override
    public <T> ClientResult<T> getFor(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(getUrl(uri));
        processCommon(spec, uri);
        return processResult(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<E> getFor(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.get()
                .uri(getUrl(uri));
        processCommon(spec, uri);
        return processResult(spec.exchange(), null, getResultType(returnTypeClass, dataTypeClass));
    }

    private ParameterizedTypeReference getResultType(Class<?> resultTypeClass, Class<?> resultDataClass) {
        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(List.class, resultDataClass);
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, inner);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        return reference;
    }


    @Override
    public <T> ClientResult<List<T>> putForList(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.put().uri(getUrl(uri)).contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        processCommon(spec, uri);
        return processResultList(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<List<E>>, E> ClientResult<List<E>> putForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.put().uri(getUrl(uri)).contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        processCommon(spec, uri);
        return processResultList(spec.exchange(), null, getResultType(returnTypeClass, dataTypeClass));
    }

    @Override
    public <T> ClientResult<T> putFor(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.put().uri(getUrl(uri)).contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        processCommon(spec, uri);
        return processResult(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<E> putFor(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.put().uri(getUrl(uri)).contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        processCommon(spec, uri);
        return processResult(spec.exchange(), null, getResultType(returnTypeClass, dataTypeClass));
    }

    @Override
    public <T> ClientResult<List<T>> deleteForList(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.delete().uri(getUrl(uri));
        processCommon(spec, uri);
        return processResultList(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<List<E>> deleteForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.delete().uri(getUrl(uri));
        processCommon(spec, uri);
        return processResultList(spec.exchange(), null, getResultType(returnTypeClass, dataTypeClass));
    }

    @Override
    public <T> ClientResult<T> deleteFor(String uri, Object params, Class<T> returnTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.delete().uri(getUrl(uri));
        processCommon(spec, uri);
        return processResult(spec.exchange(), returnTypeClass, null);
    }

    @Override
    public <T extends Result<E>, E> ClientResult<E> deleteFor(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass) {
        WebClient webClient = client(uri).build();
        WebClient.RequestHeadersSpec<?> spec = webClient.delete().uri(getUrl(uri));
        processCommon(spec, uri);
        return processResult(spec.exchange(), null, getResultType(returnTypeClass, dataTypeClass));
    }

    protected void processHeader(String uri, WebClient.RequestHeadersSpec<?> headersSpec) {

    }

    private void processCommon(WebClient.RequestHeadersSpec<?> spec, String uri) {
        processHeader(uri, spec);
        if (Objects.nonNull(SessionUtil.userId())) {
            spec.header(CommonConst.X_USER_ID, SessionUtil.userId() + "");
        }
        if (Objects.nonNull(SessionUtil.projectId())) {
            spec.header(CommonConst.X_PROJECT_ID, SessionUtil.projectId() + "");
        }
        if (Objects.nonNull(SessionUtil.tenantId())) {
            spec.header(CommonConst.X_TENANT_ID, SessionUtil.tenantId() + "");
        }

        if (isService(uri)) {
            spec.header(CommonConst.X_REQUEST_ID, MDC.get(CommonConst.REQUEST_ID));
            spec.header(CommonConst.TRACE_ID, MDC.get(CommonConst.TRACE_ID));
        }
    }
}
