package com.oceancode.cloud.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface ApiClient {
    <T> ClientResult<List<T>> postForList(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<E>, E> ClientResult<List<E>> postForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<T> postFor(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<T2>, T2> ClientResult<T2> postFor(String uri, Object params, Class<T> returnTypeClass, Class<T2> dataTypeClass);

    <T> ClientResult<List<T>> getForList(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<E>, E> ClientResult<List<E>> getForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<T> getFor(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<E>, E> ClientResult<E> getFor(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<List<T>> putForList(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<List<E>>, E> ClientResult<List<E>> putForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<T> putFor(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<E>, E> ClientResult<E> putFor(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<List<T>> deleteForList(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<E>, E> ClientResult<List<E>> deleteForList(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<T> deleteFor(String uri, Object params, Class<T> returnTypeClass);

    <T extends Result<E>, E> ClientResult<E> deleteFor(String uri, Object params, Class<T> returnTypeClass, Class<E> dataTypeClass);

    default String createUri(String url) {
        return url;
    }

    default Map<String, String> getHeaderParams() {
        return Collections.emptyMap();
    }
}
