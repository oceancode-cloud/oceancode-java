package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.GraphQLError;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public final class GraphUtil {
    private GraphUtil() {
    }

    public static Object getData(ExecutionResult result) {
        if (ValueUtil.isEmpty(result.getErrors())) {
            return result.getData();
        }
        for (GraphQLError error : result.getErrors()) {
            if (error instanceof ExceptionWhileDataFetching) {
                ExceptionWhileDataFetching ex = (ExceptionWhileDataFetching) error;
                if (Objects.nonNull(ex.getException()) && Objects.nonNull(ex.getException().getCause())) {
                    Throwable throwable = ex.getException().getCause();
                    if (throwable instanceof BusinessRuntimeException) {
                        throw (BusinessRuntimeException) throwable;
                    } else if (throwable instanceof ErrorCodeRuntimeException) {
                        throw (ErrorCodeRuntimeException) throwable;
                    }
                }

            }
        }
        ApiUtil.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ResultData.isFail();
    }
}
