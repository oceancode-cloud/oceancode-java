/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.List;
import java.util.Map;

public final class CheckApiParamUtil {
    public static void checkString(String value, int length, String field) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
        if (value.length() > length) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " length must less than " + length);
        }
    }

    public static void checkString(String value, int length, String field, boolean required) {
        if (required) {
            checkString(value, length, field);
            return;
        }
        if (value != null) {
            if (value.length() > length) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " length must less than " + length);
            }
        }
    }

    public static void checkLong(Long value, String field) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static void checkLong(Long value, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
            }
        }
    }

    public static void checkInteger(Integer value, String field) {
        if (ValueUtil.isEmpty(value)) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static void checkInteger(Integer value, String field, boolean required) {
        if (required) {
            checkInteger(value, field);
            return;
        }
    }


    public static <T extends TypeEnum<?>> void checkTypeEnum(T value, String field) {
        if (value == null || value.getValue() == null) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static <T extends TypeEnum<?>> void checkTypeEnum(T value, String field, boolean required) {
        if (required) {
            checkTypeEnum(value, field);
            return;
        }
        if (value != null) {
            if (value == null || value.getValue() == null) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
            }
        }
    }

    public static void checkBoolean(Boolean value, String field, boolean required) {
        if (required) {
            if (value == null) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
            }
        }
    }

    public static <T> void checkObject(T value, String field) {
        if (value == null) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
        }
    }

    public static <T> void checkObject(T value, String field, boolean required) {
        if (required) {
            checkObject(value, field);
            return;
        }
    }

    public static <T> void checkText(String value, int length, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
            }
        }
        if (value != null) {
            if (value.length() > length) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " length must be less than " + length);
            }
        }
    }

    public static <T> void checkStringTypeMap(Map<?, ?> value, int size, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
            }
        }

        if (value != null) {
            if (value.size() > size) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " size must be less than " + size);
            }
        }
    }

    public static void checkList(List<?> value, int size, String field, boolean required) {
        if (required) {
            if (ValueUtil.isEmpty(value)) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, field + " is required.");
            }
        }
        if (value != null) {
            if (value.size() > size) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_INVALID, field + " size must be less than " + size);
            }
        }
    }
}
