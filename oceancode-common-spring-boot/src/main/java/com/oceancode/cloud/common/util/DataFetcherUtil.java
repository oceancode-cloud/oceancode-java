package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.fetcher.DataFetcher;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.fetcher.ApiDataFetcher;

import java.util.List;

public final class DataFetcherUtil {
    private DataFetcherUtil() {

    }

    public static <T> T get(String dataId, Object params, Class<T> returnTypeClass) {
        return getDataFetcher(dataId).get(dataId, params, returnTypeClass);
    }

    public static <T> List<T> getList(String dataId, Object params, Class<T> returnTypeClass) {
        return getDataFetcher(dataId).getList(dataId, params, returnTypeClass);
    }

    public static DataFetcher getDataFetcher(String dataId) {
        String protocol = ComponentUtil.getBean(CommonConfig.class).getValue("oc.data." + dataId + ".protocol", "http://");
        if (protocol.startsWith("http:") || protocol.startsWith("https:")) {
            return new ApiDataFetcher();
        }

        throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "not found DataFetcher");
    }
}
