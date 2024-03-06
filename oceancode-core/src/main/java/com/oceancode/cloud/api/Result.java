/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api;

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
}
