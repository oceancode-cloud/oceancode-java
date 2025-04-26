package com.oceancode.cloud.api;

import com.oceancode.cloud.api.query.QueryMethod;

import java.util.List;

public interface ApiClient {
    String SERVICE_CLIENT_NAME = "service-api-client";

    <T> ClientResult<List<T>> queryForList(String uri, QueryMethod method, Class<T> dataTypeClass);

    <T> ClientResult<List<T>> queryForList(QueryMethod method, Class<T> dataTypeClass);

    <T extends Result<E>, E> ClientResult<List<E>> queryForList(String uri, QueryMethod method, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T extends Result<E>, E> ClientResult<List<E>> queryForList(QueryMethod method, Class<T> returnTypeClass, Class<E> dataTypeClass);

    <T> ClientResult<T> queryFor(String uri, List<QueryMethod> methods, Class<T> dataTypeClass);

    <T> ClientResult<T> queryFor(List<QueryMethod> methods, Class<T> dataTypeClass);

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
}
