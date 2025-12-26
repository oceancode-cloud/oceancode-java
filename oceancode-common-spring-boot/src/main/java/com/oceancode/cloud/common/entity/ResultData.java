/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ResultData<T> implements Result<T> {

    private T results;

    private String code;

    @JsonIgnore
    private Integer statusCode;

    private String message;

    @JsonIgnore
    private String requestId;
    private Long total;

    @JsonIgnore
    private List<T> list;

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
        setCode(errorCode.getShortCode());
        setMessage(errorCode.getMessage());
        return this;
    }

    public ResultData<T> message(String message, Object... args) {
        if (null == args || args.length == 0 || message == null) {
            setMessage(message);
            return this;
        }
        setMessage(String.format(message, args));
        return this;
    }

    public ResultData<T> data(T data) {
        setResults(data);
        return this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        boolean ret = Objects.isNull(this.getStatusCode()) || getStatusCode() == 200;
        if (!ret) {
            return false;
        }
        return CommonErrorCode.SUCCESS.getShortCode().equals(getCode()) || "0".equals(getCode())
                || ValueUtil.isEmpty(getCode());
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

    @JsonIgnore
    public boolean isList() {
        return results == null && list != null && list != Collections.emptyList();
    }

    @JsonIgnore
    public List<T> getResultList() {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    @JsonIgnore
    public void setResultList(List<T> list) {
        this.list = list;
    }

    @JsonIgnore
    public ResultData<T> statusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @JsonIgnore
    public Integer getStatusCode() {
        return statusCode;
    }

    @JsonIgnore
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @JsonIgnore
    public String getRequestId() {
        return requestId;
    }

    @JsonIgnore
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
