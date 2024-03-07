/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.Collection;

/**
 * Assertion utility class that assists in validating arguments.
 *
 * <p>Useful for identifying programmer errors early and clearly at runtime.
 *
 * @author Dynamic Gen
 * @since 1.0
 */
public final class Assert {

    public static void notNull(Object object, ErrorCode errorCode, String message) {
        if (null == object) {
            throw new BusinessRuntimeException(errorCode, message);
        }
    }

    public static <T> T checkResult(T object, ErrorCode errorCode, boolean throwEx) {
        return checkResult(object, errorCode, throwEx, "");
    }

    public static <T> T checkResult(T object, ErrorCode errorCode, boolean throwEx, String message) {
        if (!throwEx) {
            return object;
        }
        if (null == object) {
            throw new BusinessRuntimeException(errorCode, message);
        }
        return object;
    }

    public static void notEmpty(Collection<?> collection, ErrorCode errorCode, String message) {
        if (null == collection || collection.isEmpty()) {
            throw new BusinessRuntimeException(errorCode, message);
        }
    }

    public static void checkResultDataSize(int size, int maxSize) {
        if (size > maxSize) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "result size must be less than " + CommonConst.DEFAULT_RESULT_RECORDS_SIZE + ",actual:" + size);
        }
    }

    public static void checkResultDataSize(Collection<?> resultList, int maxSize) {
        if (resultList == null) {
            return;
        }
        checkResultDataSize(resultList.size(), maxSize);
    }

    public static void checkParamCapacitySize(int size, int maxSize) {
        if (size > maxSize) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "param size must be less than " + CommonConst.DEFAULT_RESULT_RECORDS_SIZE + ",actual:" + size);
        }
    }

    public static void checkParamCapacitySize(Collection<?> collection) {
        checkParamCapacitySize(collection, CommonConst.DEFAULT_RESULT_RECORDS_SIZE);
    }

    public static void checkParamCapacitySize(Collection<?> collection, int maxSize) {
        if (collection == null) {
            return;
        }
        checkParamCapacitySize(collection.size(), maxSize);
    }
}
