package com.oceancode.cloud.api.fetcher;

import java.util.List;

public interface DataFetcher {
    <T> T get(String dataId, Object params, Class<T> returnTypeClass);

    <T> List<T> getList(String dataId, Object params, Class<T> returnTypeClass);
}
