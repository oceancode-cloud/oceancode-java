/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.models.entity.ResultData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <B>ValueUtil</B>
 *
 * <p>
 * This class is a util.
 * </p>
 *
 * @author qinjiawang
 * @since 1.0
 */
public final class ValueUtil {
    private ValueUtil() {
    }

    /**
     * Check whether the given object (possibly a {@code String}) is empty.
     *
     * @param str string
     * @return true if str is empty else false
     */
    public static boolean isEmpty(String str) {
        return !isNotEmpty(str);
    }

    public static boolean isEmpty(Integer value) {
        return value == null;
    }

    public static boolean isEmpty(Long value) {
        return value == null;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?,?> map) {
        return map == null || map.isEmpty();
    }


    public static boolean isEmpty(ResultData<?> result) {
        return result == null || result.getResults() == null;
    }

    public static boolean isNotEmpty(String value) {
        boolean ret = value != null && !value.isEmpty();
        if (!ret) {
            return false;
        }
        int strLen = value.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static boolean isNotEmpty(Collection<?> value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(List<?> value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(Map<?, ?> value) {
        return !isEmpty(value);
    }

    public static boolean isNotEmpty(Long value) {
        return value != null;
    }

    public static boolean isObjectNotEmpty(Object value) {
        return !isObjectEmpty(value);
    }

    public static boolean isObjectEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return isEmpty((String) value);
        } else if (value instanceof Map) {
            return isEmpty((Map<?,?>) value);
        } else if (value instanceof Collection) {
            return isEmpty((Collection<?>) value);
        }

        return false;
    }

    /**
     * get array element by index
     *
     * @param array array
     * @param pos   array index
     * @return if pos is range in (0,array.size()) return array.get(pos) else return null.
     */
    public static <T> T getElementByIndex(T[] array, int pos) {
        return getElementByIndex(array, pos, null);
    }

    /**
     * get array element by index
     *
     * @param array        array
     * @param pos          array index
     * @param defaultValue defaultValue
     * @return if pos is range in (0,array.size()) return array.get(pos) else return null.
     */
    public static <T> T getElementByIndex(T[] array, int pos, T defaultValue) {
        if (Objects.isNull(array) || pos > array.length - 1) {
            return defaultValue;
        }
        T value = array[pos];
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * get array element by index
     *
     * @param values values
     * @return the first not empty element.
     */
    public static <T> T getFirstNotEmptyElement(List<T> values) {
        if (Objects.isNull(values) || values.isEmpty()) {
            return null;
        }
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    public static <T> T getValue(Object data, Supplier<T> supplier, T defaultValue) {
        if (data == null) {
            return defaultValue;
        }
        T value = supplier.get();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static <T> T getValue(Object data, Supplier<T> supplier) {
        return getValue(data, supplier, null);
    }

    public static boolean isTrue(Boolean value) {
        return Objects.nonNull(value) && value;
    }

    public static Boolean isFalse(Boolean value) {
        return Objects.isNull(value) || !value;
    }
}
