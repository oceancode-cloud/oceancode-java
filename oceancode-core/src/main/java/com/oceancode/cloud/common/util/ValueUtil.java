/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern middleLinePattern = Pattern.compile("-(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

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

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(Timestamp timestamp) {
        return timestamp == null;
    }

    public static boolean isEmpty(TypeEnum<?> type) {
        return type == null;
    }

    public static boolean isEmpty(Boolean value) {
        return null == value;
    }

    public static boolean isEmpty(Date value) {
        return null == value;
    }

    public static boolean isEmpty(BigDecimal value) {
        return null == value;
    }

    public static boolean isEmpty(LocalDateTime value) {
        return null == value;
    }

    public static boolean isEmpty(LocalDate value) {
        return null == value;
    }

    public static boolean isEmpty(LocalTime value) {
        return null == value;
    }

    public static boolean isEmpty(String[] value) {
        return null == value || value.length == 0;
    }

    public static boolean isEmpty(byte[] value) {
        return null == value || value.length == 0;
    }

    public static boolean isEmpty(char[] value) {
        return null == value || value.length == 0;
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

    public static boolean isNotEmpty(Timestamp timestamp) {
        return timestamp != null;
    }


    public static boolean isNotEmpty(TypeEnum<?> type) {
        return type != null;
    }

    public static boolean isNotEmpty(Integer value) {
        return value != null;
    }

    public static boolean isNotEmpty(Boolean value) {
        return value != null;
    }

    public static boolean isNotEmpty(Date value) {
        return null != value;
    }

    public static boolean isNotEmpty(BigDecimal value) {
        return null != value;
    }

    public static boolean isNotEmpty(String[] value) {
        return null != value && value.length > 0;
    }

    public static boolean isNotEmpty(char[] value) {
        return null != value && value.length > 0;
    }

    public static boolean isNotEmpty(byte[] value) {
        return null != value && value.length > 0;
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
            return isEmpty((Map<?, ?>) value);
        } else if (value instanceof Collection) {
            return isEmpty((Collection<?>) value);
        }

        return false;
    }

    public static boolean isEquals(Long val1, Long val2) {
        return (val1 == null && val2 == null) || val1.equals(val2);
    }

    public static boolean isEquals(String val1, String val2) {
        return (val1 == null && val2 == null) || val1.equals(val2);
    }

    public static boolean isEquals(Integer val1, Integer val2) {
        return (val1 == null && val2 == null) || val1.equals(val2);
    }

    public static boolean isEquals(BigDecimal val1, BigDecimal val2) {
        return (val1 == null && val2 == null) || val1.compareTo(val2) == 0;
    }

    public static boolean isEquals(TypeEnum<?> val1, TypeEnum<?> val2) {
        return (val1 == null && val2 == null) || val1.equals(val2);
    }

    public static boolean isBetween(Timestamp val, Timestamp lower, Timestamp upper) {
        if (val == null || lower == null || upper == null) {
            return false;
        }
        return val.before(lower) && val.after(upper);
    }

    public static boolean isBetween(Long val, Long lower, Long upper) {
        if (val == null || lower == null || upper == null) {
            return false;
        }
        return val >= lower && val < upper;
    }

    public static boolean isBetween(Integer val, Integer lower, Integer upper) {
        if (val == null || lower == null || upper == null) {
            return false;
        }
        return val >= lower && val < upper;
    }

    public static boolean isBetween(BigDecimal val, BigDecimal lower, BigDecimal upper) {
        if (val == null || lower == null || upper == null) {
            return false;
        }
        return val.compareTo(lower) >= 0 && val.compareTo(upper) < 0;
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

    public static Object getValueFromList(List<?> list, int pos) {
        if (Objects.isNull(list)) {
            return null;
        }
        if (pos >= 0 && pos < list.size()) {
            return list.get(pos);
        }
        return null;
    }

    public static String[] splitWithSeparator(String str) {
        if (isEmpty(str)) {
            return null;
        }
        return str.split(String.valueOf((char) 10));
    }

    public static boolean isTrue(Boolean value) {
        return Objects.nonNull(value) && value;
    }

    public static Boolean isFalse(Boolean value) {
        return Objects.isNull(value) || !value;
    }

    public static <K, V> Map<K, V> putMapElements(Map<K, V> sourceMap, Map<K, V> dataMap) {
        if (Objects.isNull(dataMap)) {
            return sourceMap;
        }
        Map<K, V> map = sourceMap == null ? new HashMap<>() : sourceMap;
        map.putAll(dataMap);

        return map;
    }

    public static <T, E> E getValue(T object, E defaultValue, Function<T, E> function) {
        if (Objects.isNull(object)) {
            return defaultValue;
        }
        E element = function.apply(object);
        if (Objects.isNull(element)) {
            return defaultValue;
        }
        return element;
    }

    public static <T, E> E getValue(T object, Function<T, E> function) {
        return getValue(object, (E) null, function);
    }

    public static String underlineToCamel(String line) {
        Matcher matcher = linePattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String camelToUnderline(String line) {
        return camelTo(line, "_");
    }

    public static String camelTo(String line, String ch) {
        Matcher matcher = humpPattern.matcher(line);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ch + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (Objects.isNull(result)) {
            return result;
        }
        if (result.startsWith(ch)) {
            return result.substring(1);
        }
        return result;
    }

    public static String toCamel(String line) {
        if (line.contains("_")) {
            return underlineToCamel(line);
        }
        if (line.contains("-")) {
            Matcher matcher = middleLinePattern.matcher(line);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
            String code = sb.toString();
            if (code.length() > 0) {
                return code.substring(0, 1).toUpperCase() + code.substring(1);
            }
            return code.substring(0, 1).toUpperCase();
        }
        return line;
    }

    public static byte[] compressData(String rawData) {
        if (isEmpty(rawData)) {
            return null;
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(byteArrayOutputStream)) {
            gzip.write(rawData.getBytes(StandardCharsets.UTF_8));
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    public static String unCompressData(byte[] compressData) {
        if (Objects.isNull(compressData)) {
            return null;
        }
        if (compressData.length == 0) {
            return "";
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); InputStream in = new ByteArrayInputStream(compressData); GZIPInputStream gzip = new GZIPInputStream(in)) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer)) != 1) {
                outputStream.write(buffer, 0, n);
            }
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }

    }

    public static Object getValueByKeyPath(Map<String, Object> map, String keyPaths) {
        if (isEmpty(keyPaths)) {
            return null;
        }
        String[] split = keyPaths.split("[.]");
        Object cur = map;
        int count = 0;
        while (cur != null) {
            if (count >= split.length) {
                break;
            }
            if (!(cur instanceof Map<?, ?> m)) {
                return null;
            }
            String key = split[count];
            cur = m.get(key);
            count++;
        }
        if (count == split.length) {
            return cur;
        }
        return null;
    }

    public static String ltrim(String s) {
        if (Objects.isNull(s)) {
            return s;
        }
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        String ltrim = s.substring(i);
        return ltrim;
    }

    public static String rtrim(String s) {
        if (Objects.isNull(s)) {
            return s;
        }
        int i = s.length() - 1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        String rtrim = s.substring(0, i + 1);
        return rtrim;
    }
}
