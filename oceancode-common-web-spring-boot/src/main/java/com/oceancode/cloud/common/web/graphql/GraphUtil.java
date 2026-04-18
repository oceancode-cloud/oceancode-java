package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.validation.ValidationError;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public final class GraphUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(GraphUtil.class);

    private GraphUtil() {
    }

    public static Object getData(ExecutionResult result) {
        if (ValueUtil.isEmpty(result.getErrors())) {
            return result.getData();
        }
        for (GraphQLError error : result.getErrors()) {

            if (error instanceof ExceptionWhileDataFetching) {
                ExceptionWhileDataFetching ex = (ExceptionWhileDataFetching) error;
                LOGGER.error("error", ex.getException());
                if (Objects.nonNull(ex.getException()) && Objects.nonNull(ex.getException().getCause())) {
                    Throwable throwable = ex.getException().getCause();
                    if (throwable instanceof BusinessRuntimeException) {
                        throw (BusinessRuntimeException) throwable;
                    } else if (throwable instanceof ErrorCodeRuntimeException) {
                        throw (ErrorCodeRuntimeException) throwable;
                    } else if (throwable instanceof InvocationTargetException invocationTargetException) {
                        if (invocationTargetException.getTargetException() instanceof ErrorCodeRuntimeException errorCodeRuntimeException) {
                            throw errorCodeRuntimeException;
                        }
                    }
                }
            } else if (error instanceof ValidationError validationError) {
                throw new BusinessRuntimeException(CommonErrorCode.API_NOT_FOUND, validationError.getDescription());
            }
        }
        ApiUtil.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ResultData.isFail();
    }
}
