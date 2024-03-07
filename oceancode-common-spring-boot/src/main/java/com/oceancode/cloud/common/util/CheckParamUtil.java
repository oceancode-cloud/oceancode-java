/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.entity.PartFile;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.entity.StringTypeMap;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;

import java.util.List;

public final class CheckParamUtil {
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
