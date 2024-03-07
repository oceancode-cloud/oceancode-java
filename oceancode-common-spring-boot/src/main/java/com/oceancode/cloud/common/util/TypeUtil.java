/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.math.BigDecimal;
import java.util.Objects;

public final class TypeUtil {
    private TypeUtil() {
    }

    public static String convertToString(Object value) {
        return convertToString(value, (String) null);
    }

    public static String convertToString(Object value, String defaultValue) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        String result = value.toString();
        return ValueUtil.isEmpty(result) ? defaultValue : result;
    }

    public static Long convertToLong(Object value) {
        return convertToLong(value, null);
    }

    public static Long convertToLong(Object value, Long defaultValue) {
        if (ValueUtil.isObjectEmpty(value)) {
            return defaultValue;
        }
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        } else if (value instanceof Number) {
            return Long.parseLong(value + "");
        }

        return defaultValue;
    }

    public static Integer convertToInteger(Object value) {
        return convertToInteger(value, null);
    }

    public static Integer convertToInteger(Object value, Integer defaultValue) {
        if (ValueUtil.isObjectEmpty(value)) {
            return defaultValue;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Number) {
            return Integer.parseInt(value + "");
        }

        return defaultValue;
    }

    public static BigDecimal convertToBigDecimal(Object value) {
        return convertToBigDecimal(value, null);
    }

    public static BigDecimal convertToBigDecimal(Object value, BigDecimal defaultValue) {
        if (ValueUtil.isObjectEmpty(value)) {
            return defaultValue;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        } else if (value instanceof Number) {
            return new BigDecimal(value + "");
        }

        return defaultValue;
    }

    public static <T> T convertToBySimpleName(String classSimpleName, Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (String.class.getSimpleName().equals(classSimpleName)) {
            return (T) convertToString(value);
        } else if (Integer.class.getSimpleName().equals(classSimpleName)) {
            return (T) convertToInteger(value);
        } else if (Long.class.getSimpleName().equals(classSimpleName)) {
            return (T) convertToLong(value);
        } else if (BigDecimal.class.getSimpleName().equals(classSimpleName)) {
            return (T) convertToBigDecimal(value);
        }

        throw new BusinessRuntimeException(CommonErrorCode.ERROR, "unsupport %s convert to %s", value.getClass().getSimpleName(), classSimpleName);
    }
}