/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;

import java.util.List;

/**
 * @param <T> data type
 * @author qinjiawang
 */
public interface Result<T> {

    /**
     * get data
     *
     * @return data
     */
    T getResults();

    boolean isSuccess();

    String getMessage();

    String getCode();

    default List<String> getHeader(String key) {
        return null;
    }

    default boolean isAccessDenied() {
        String code = this.getCode();
        boolean ret = CommonErrorCode.ACCESS_DENIED.getCode().equals(code) ||
                CommonErrorCode.AUTHORIZATION_INVALID.getCode().equals(code) ||
                CommonErrorCode.NOT_LOGIN.getCode().equals(code) ||
                CommonErrorCode.AUTHORIZATION_MISSING.getCode().equals(code);
        return ret;
    }
}
