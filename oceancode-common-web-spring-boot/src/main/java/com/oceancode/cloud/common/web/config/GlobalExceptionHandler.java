/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.config;

import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(BusinessRuntimeException.class)
    public ResultData<?> businessRuntimeException(HttpServletRequest request, Exception exception) throws Exception {
        ResultData<?> result = ResultData.isFail();
        BusinessRuntimeException businessRuntimeException = (BusinessRuntimeException) exception;
        if (businessRuntimeException.getCode().isClientError()) {
            result.code(businessRuntimeException.getCode())
                    .message(businessRuntimeException.getMessage());
            if (CommonErrorCode.NOT_LOGIN.equals(businessRuntimeException.getCode())) {
                ApiUtil.getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (CommonErrorCode.PERMISSION_DENIED.equals(businessRuntimeException.getCode())) {
                ApiUtil.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                ApiUtil.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            LOGGER.warn("client error,requestUrl:" + request.getRequestURI() + ",code:" + result.getCode() + ",message:" + result.getMessage());
        } else {
            ApiUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("server error,requestUrl:" + request.getRequestURI(), exception);
        }
        return result;
    }

    @ResponseBody
    @ExceptionHandler(ErrorCodeRuntimeException.class)
    public ResultData<?> errorCodeRuntimeException(HttpServletRequest request, Exception exception) throws Exception {
        ResultData<?> result = ResultData.isFail();
        ErrorCodeRuntimeException businessRuntimeException = (ErrorCodeRuntimeException) exception;
        result.setCode(businessRuntimeException.getErrorCode());
        result.message(businessRuntimeException.getMessage());
        LOGGER.warn("client error,requestUrl:" + request.getRequestURI() + ",code:" + result.getCode() + ",message:" + result.getMessage());
        if (ValueUtil.isNotEmpty(result.getCode()) && result.getCode().startsWith(CommonConst.CLIENT_ERROR_CODE_PREFIX)) {
            ApiUtil.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            ApiUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultData<?> exceptionHandler(HttpServletRequest request, Exception exception) {
        ResultData<?> result = ResultData.isFail();
        LOGGER.error("server error,requestUrl:" + request.getRequestURI(), exception);
        ApiUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return result;
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResultData<?> runtimeException(HttpServletRequest request, Exception exception) {
        ResultData<?> result = ResultData.isFail();
        ApiUtil.getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        LOGGER.error("server error,requestUrl:" + request.getRequestURI(), exception);
        return result;
    }
}