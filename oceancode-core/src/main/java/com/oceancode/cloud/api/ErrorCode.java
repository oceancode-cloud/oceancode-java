/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api;

/**
 * ErrorCode
 *
 * <p>Useful for define Api errors when occurs some exception at runtime.
 *
 * @author qinjiawang
 * @since 1.0
 */
public interface ErrorCode {

    /**
     * error code
     *
     * @return error code
     */
    String getCode();

    /**
     * error message
     *
     * @return error message
     */
    String getMessage();

    /**
     * error code it use to success status or not.
     *
     * @return true if handle successfully else false
     */
    boolean isSuccess();

    /**
     * client error
     * that can be returned to client.
     *
     * @return true if user error code else server error code
     */
    boolean isClientError();

    /**
     * server error
     * that can only be log in log file rather than to return client.
     *
     * @return true if server error code else false
     */
    boolean isServerError();
}
