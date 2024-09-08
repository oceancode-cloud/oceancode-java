/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.api.validation.Validator;
import com.oceancode.cloud.common.entity.PartFile;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.entity.StringTypeMap;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CheckParamUtil {
    public static void notNull(Object value, String field) {
        if (value == null) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static void notEmpty(Boolean value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(Integer value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(Long value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(LocalDate value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(LocalTime value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(BigDecimal value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(PartFile value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(Collection<?> value, String field) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static void notEmpty(Map<?, ?> value, String field) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }


    public static <T extends Validator> void checkParam(T param) {
        if (param == null) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "param is required.");
        }
        param.validate();
    }

    public static void checkPrimaryKey(Long value, String field) {
        notEmpty(value, field);
        if (value < 1) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " invalid.");
        }
    }

    public static void checkPrimaryKey(Integer value, String field) {
        notNull(value, field);
        if (value < 1) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " invalid.");
        }
    }

    public static void notEmpty(LocalDateTime value, String field) {
        notNull(value, field);
    }

    public static void notEmpty(String value, String field) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static void checkString(String value, String field, int maxLength) {
        notEmpty(value, field);
        if (value != null) {
            if (value.length() > maxLength) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, "the size of " + field + " is too large.");
            }
        }
    }

    public static boolean checkString(ResultData<?> result, String value, int length, String field) {
        if (ValueUtil.isEmpty(value)) {
            result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
            return false;
        }
        if (value.length() > length) {
            result.code(CommonErrorCode.PARAMETER_INVALID).message(field + " length must less than " + length);
            return false;
        }
        return true;
    }

    public static boolean checkString(ResultData<?> result, String value, int length, String field, boolean required) {
        if (required) {
            return checkString(result, value, length, field);
        }
        if (value != null) {
            if (value.length() > length) {
                result.code(CommonErrorCode.PARAMETER_INVALID).message(field + " length must less than " + length);
                return false;
            }
        }
        return true;
    }

    public static boolean checkLong(ResultData<?> result, Long value, String field) {
        if (ValueUtil.isEmpty(value)) {
            result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
            return false;
        }
        return true;
    }

    public static boolean checkLong(ResultData<?> result, Long value, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
                return false;
            }
        }
        return true;
    }

    public static boolean checkInteger(ResultData<?> result, Integer value, String field) {
        if (ValueUtil.isEmpty(value)) {
            result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
            return false;
        }

        return true;
    }

    public static boolean checkInteger(ResultData<?> result, Integer value, String field, boolean required) {
        if (required) {
            return checkInteger(result, value, field);
        }
        return true;
    }

    public static boolean checkPartFile(ResultData<?> result, PartFile value, String field, boolean required) {
        if (!required) {
            return true;
        }
        if (value == null || value.getInputStream() == null || ValueUtil.isEmpty(value.getFilename()) || value.getSize() == null) {
            result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
            return false;
        }
        if (value.getSize() <= 0) {
            result.code(CommonErrorCode.PARAMETER_INVALID).message(field + " is invalid.");
            return false;
        }
        return true;
    }

    public static <T extends TypeEnum<?>> boolean checkTypeEnum(ResultData<?> result, T value, String field) {
        if (value == null || value.getValue() == null) {
            result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
            return false;
        }
        return true;
    }

    public static <T extends TypeEnum<?>> boolean checkTypeEnum(ResultData<?> result, T value, String field, boolean required) {
        if (required) {
            return checkTypeEnum(result, value, field);
        }
        if (value != null) {
            if (value == null || value.getValue() == null) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
                return false;
            }
        }

        return true;
    }

    public static boolean checkBoolean(ResultData<?> result, Boolean value, String field, boolean required) {
        if (required) {
            if (value == null) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
                return false;
            }
        }
        return true;
    }

    public static <T> boolean checkObject(ResultData<?> result, T value, String field) {
        if (value == null) {
            result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
            return false;
        }
        return true;
    }

    public static <T> boolean checkObject(ResultData<?> result, T value, String field, boolean required) {
        if (required) {
            return checkObject(result, value, field);
        }
        return true;
    }

    public static <T> boolean checkText(ResultData<T> result, String value, int length, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
                return false;
            }
        }
        if (value != null) {
            if (value.length() > length) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " length must be less than " + length);
                return false;
            }
        }

        return true;
    }

    public static <T> boolean checkStringTypeMap(ResultData<T> result, StringTypeMap value, int size, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
                return false;
            }
        }

        if (value != null) {
            if (value.size() > size) {
                result.code(CommonErrorCode.PARAMETER_INVALID).message(field + " size must be less than " + size);
                return false;
            }
        }

        return true;
    }

    public static boolean checkList(ResultData<?> result, List<?> value, int size, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                result.code(CommonErrorCode.PARAMETER_MISSING).message(field + " is required.");
                return false;
            }
        }
        if (value != null) {
            if (value.size() > size) {
                result.code(CommonErrorCode.PARAMETER_INVALID).message(field + " size must be less than " + size);
                return false;
            }
        }

        return true;
    }
}
