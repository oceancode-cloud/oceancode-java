/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;

public class ResultData<T> implements Result<T> {

    private T results;

    private String code;

    private String message;

    private Long total;

    private ResultData() {
    }

    public static <T> ResultData<T> isOk(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.code(CommonErrorCode.SUCCESS);
        resultData.setResults(data);
        return resultData;
    }

    public static <T> ResultData<T> isOk() {
        return isOk(null);
    }


    public static <T> ResultData<T> isFail() {
        return isFail(CommonErrorCode.ERROR);
    }

    public static <T> ResultData<T> isFail(ErrorCode errorCode) {
        ResultData<T> resultData = isOk();
        resultData.code(errorCode);
        return resultData;
    }

    public static <T> ResultData<T> isFail(ErrorCode errorCode, String message, Object... args) {
        ResultData<T> resultData = isFail(errorCode);
        resultData.message(message, args);
        return resultData;
    }

    public ResultData<T> code(ErrorCode errorCode) {
        setCode(errorCode.getCode());
        setMessage(errorCode.getMessage());
        return this;
    }

    public ResultData<T> message(String message, Object... args) {
        setMessage(String.format(message, args));
        return this;
    }

    public ResultData<T> data(T data) {
        setResults(data);
        return this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return CommonErrorCode.SUCCESS.getCode().equals(getCode());
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
