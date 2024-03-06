/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.exception;

import com.oceancode.cloud.api.ErrorCode;

/**
 * @author qinjiawang
 */
public class BusinessRuntimeException extends ErrorCodeRuntimeException{

    private ErrorCode code;

    private String message;

    public BusinessRuntimeException(ErrorCode code, String message, Object... args) {
        super(String.format(message, args));
        this.code = code;
        this.message = String.format(message, args);
    }

    public BusinessRuntimeException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
        this.message = code.getMessage();
    }

    public BusinessRuntimeException(ErrorCode code, Throwable throwable) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ErrorCode getCode() {
        return code;
    }

    @Override
    public String getErrorCode() {
        return code.getCode();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
