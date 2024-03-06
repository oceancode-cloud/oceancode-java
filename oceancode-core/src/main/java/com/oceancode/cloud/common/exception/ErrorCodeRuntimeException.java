/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.exception;

/**
 * @author qinjiawang
 */
public class ErrorCodeRuntimeException extends RuntimeException {

    private String errorCode;
    private String message;

    public ErrorCodeRuntimeException(String message) {
        super(message);
    }

    public ErrorCodeRuntimeException(Throwable cause) {
        super(cause);
    }

    public ErrorCodeRuntimeException(String code, String message) {
        super(message);
        this.errorCode = code;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
