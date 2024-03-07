/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ServiceUtil {
    private static String parseServiceId(String serviceId) {
        if (ValueUtil.isEmpty(serviceId)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "serviceId is not empty");
        }
        String paramString = "";
        if (serviceId.contains("?")) {
            paramString = serviceId.substring(serviceId.indexOf("?"));
            serviceId = serviceId.substring(0, serviceId.indexOf("?"));
        }
        String[] ids = StringUtils.tokenizeToStringArray(serviceId, "_");
        if (ids.length != 4) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, serviceId + " invalid.");
        }
        String serviceName = ids[0] + "_" + ids[1];
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        String requestUrl = commonConfig.getValue("app.service." + serviceName, () -> "http://" + serviceName + "/api/");
        if (ValueUtil.isEmpty(requestUrl)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "request url is not empty.");
        }
        if (!requestUrl.endsWith("/")) {
            return requestUrl + "/" + serviceId + paramString;
        }
        return requestUrl + serviceId + paramString;
    }


    private static String parseUrl(String serviceId) {
        return parseServiceId(serviceId);
    }

    private static String parseUrlWithParam(String serviceId, Map<String, Object> params) {
        String url = parseUrl(serviceId);
        if (ValueUtil.isNotEmpty(params)) {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (!url.contains("?")) {
                sb.append("?");
            } else {
                if (!url.endsWith("&")) {
                    sb.append("&");
                }
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (Objects.isNull(entry.getValue())) {
                    continue;
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url = sb.toString();
        }
        return url;
    }

    public static <E, T extends Result<E>> Result<List<E>> getForList(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(List.class, resultDataClass);
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, inner);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Object> entity = ComponentUtil.getBean(RestTemplate.class).exchange(parseUrlWithParam(serviceId, params), HttpMethod.GET, new HttpEntity<>(null, headers), reference);
        return (Result<List<E>>) entity.getBody();
    }

    public static <E, T extends Result<E>> Result<List<E>> getForList(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return getForList(serviceId, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<List<E>> getForList(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return getForList(serviceId, params, ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> getForList(String serviceId, Class<E> resultDataClass) {
        return getForList(serviceId, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> getForModel(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, resultDataClass);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Object> entity = ComponentUtil.getBean(RestTemplate.class).exchange(parseUrlWithParam(serviceId, params), HttpMethod.GET, new HttpEntity<>(null, headers), reference);
        return (Result<E>) entity.getBody();
    }

    public static <E, T extends Result<E>> Result<E> getForModel(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return getForModel(serviceId, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<E> getForModel(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return getForModel(serviceId, params, ResultData.class, resultDataClass);
    }

    public static <E> Result<E> getForModel(String serviceId, Class<E> resultDataClass) {
        return getForModel(serviceId, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    private static <E, T extends Result<E>> Result<List<E>> requestForList(HttpMethod httpMethod, String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType inner = typeFactory.constructParametricType(List.class, resultDataClass);
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, inner);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Object> entity = ComponentUtil.getBean(RestTemplate.class).exchange(parseUrlWithParam(serviceId, params), httpMethod, new HttpEntity<>(requestBody, headers), reference);
        return (Result<List<E>>) entity.getBody();
    }

    private static <E, T extends Result<E>> Result<E> requestForModel(HttpMethod httpMethod, String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, resultDataClass);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Object> entity = ComponentUtil.getBean(RestTemplate.class).exchange(parseUrlWithParam(serviceId, params), httpMethod, new HttpEntity<>(requestBody, headers), reference);
        return (Result<E>) entity.getBody();
    }

    public static <E, T extends Result<E>> Result<List<E>> postForList(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.POST, serviceId, params, requestBody, resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> postForList(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.POST, serviceId, params, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> postForList(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.POST, serviceId, Collections.emptyMap(), Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<List<E>> postForList(String serviceId, Class<E> resultDataClass) {
        return requestForList(HttpMethod.POST, serviceId, Collections.emptyMap(), Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> postForList(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<E> resultDataClass) {
        return requestForList(HttpMethod.POST, serviceId, params, requestBody, ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> postForList(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return requestForList(HttpMethod.POST, serviceId, params, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> postForModel(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.POST, serviceId, params, requestBody, resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> postForModel(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.POST, serviceId, params, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> postForModel(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.POST, serviceId, Collections.emptyMap(), Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<E> postForModel(String serviceId, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.POST, serviceId, Collections.emptyMap(), Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E> Result<E> postForModel(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.POST, serviceId, params, requestBody, ResultData.class, resultDataClass);
    }

    public static <E> Result<E> postForModel(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.POST, serviceId, params, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> putForList(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.PUT, serviceId, params, requestBody, resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> putForList(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.PUT, serviceId, params, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> putForList(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.PUT, serviceId, Collections.emptyMap(), Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<List<E>> putForList(String serviceId, Class<E> resultDataClass) {
        return requestForList(HttpMethod.PUT, serviceId, Collections.emptyMap(), Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> putForList(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<E> resultDataClass) {
        return requestForList(HttpMethod.PUT, serviceId, params, requestBody, ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> putForList(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return requestForList(HttpMethod.PUT, serviceId, params, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> putForModel(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.PUT, serviceId, params, requestBody, resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> putForModel(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.PUT, serviceId, params, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> putForModel(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.PUT, serviceId, Collections.emptyMap(), Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<E> putForModel(String serviceId, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.PUT, serviceId, Collections.emptyMap(), Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E> Result<E> putForModel(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.PUT, serviceId, params, requestBody, ResultData.class, resultDataClass);
    }

    public static <E> Result<E> putForModel(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.PUT, serviceId, params, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> deleteForList(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.DELETE, serviceId, params, requestBody, resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> deleteForList(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.DELETE, serviceId, params, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<List<E>> deleteForList(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForList(HttpMethod.DELETE, serviceId, Collections.emptyMap(), Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<List<E>> deleteForList(String serviceId, Class<E> resultDataClass) {
        return requestForList(HttpMethod.DELETE, serviceId, Collections.emptyMap(), Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> deleteForList(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<E> resultDataClass) {
        return requestForList(HttpMethod.DELETE, serviceId, params, requestBody, ResultData.class, resultDataClass);
    }

    public static <E> Result<List<E>> deleteForList(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return requestForList(HttpMethod.DELETE, serviceId, params, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> deleteForModel(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.DELETE, serviceId, params, requestBody, resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> deleteForModel(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.DELETE, serviceId, params, Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> deleteForModel(String serviceId, Class<T> resultTypeClass, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.DELETE, serviceId, Collections.emptyMap(), Collections.emptyMap(), resultTypeClass, resultDataClass);
    }

    public static <E> Result<E> deleteForModel(String serviceId, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.DELETE, serviceId, Collections.emptyMap(), Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E> Result<E> deleteForModel(String serviceId, Map<String, Object> params, Map<String, Object> requestBody, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.DELETE, serviceId, params, requestBody, ResultData.class, resultDataClass);
    }

    public static <E> Result<E> deleteForModel(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return requestForModel(HttpMethod.DELETE, serviceId, params, Collections.emptyMap(), ResultData.class, resultDataClass);
    }

    public static <E, T extends Result<E>> Result<E> uploadFile(String serviceId, Map<String, Object> params, Class<T> resultTypeClass, Class<E> resultDataClass) {
        TypeFactory typeFactory = JsonUtil.getObjectMapper().getTypeFactory();
        JavaType javaType = typeFactory.constructParametricType(resultTypeClass, resultDataClass);
        ParameterizedTypeReference<Object> reference = ParameterizedTypeReference.forType(javaType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String,Object> fileParam = new LinkedMultiValueMap();
        if (Objects.nonNull(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (Objects.isNull(value)) {
                    continue;
                }
                if (value instanceof List) {
                    List<?> files = (List<?>) value;
                    for (Object file : files) {
                        fileParam.add(entry.getKey(), file);
                    }
                    continue;
                }
                fileParam.add(entry.getKey(), entry.getValue());
            }
        }

        ResponseEntity<Object> entity = ComponentUtil.getBean(RestTemplate.class).exchange(parseUrl(serviceId), HttpMethod.POST, new HttpEntity<>(fileParam, headers), reference);
        return (Result<E>) entity.getBody();
    }

    public static <E> Result<E> uploadFile(String serviceId, Map<String, Object> params, Class<E> resultDataClass) {
        return uploadFile(serviceId, params, ResultData.class, resultDataClass);
    }

    public static Result<Void> uploadFile(String serviceId, Map<String, Object> params) {
        return uploadFile(serviceId, params, ResultData.class, Void.class);
    }
}
