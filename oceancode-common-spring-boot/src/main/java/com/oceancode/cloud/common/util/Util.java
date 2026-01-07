package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.list.WrapperArrayList;
import com.oceancode.cloud.entity.Tuple2;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Util extends ValueUtil {

    private Util() {
    }

    public static <T> T convertTo(Object value, Class<T> returnType) {
        Object object = convert(value, returnType);
        return returnType.cast(object);
    }

    public static void convertMapFieldTo(Map<String, Object> value, String property, Class<?> returnType) {
        if (isEmpty(value)) {
            return;
        }
        if (!value.containsKey(property)) {
            return;
        }
        Object object = value.get(property);
        Object target = convert(object, returnType);
        value.put(property, target);
    }

    public static <T> List<T> convertToList(Object value, Class<T> returnType) {
        Object object = convert(value, returnType);
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof List<?> list) {
            return list.stream().map(returnType::cast).toList();
        }

        return List.of(returnType.cast(object));
    }

    public static Object convert(Object value, Class<?> returnType) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (returnType.isInstance(value)) {
            return returnType.cast(value);
        }
        Object targetValue = value;
        if (Long.class.equals(returnType)) {
            if (value instanceof String val) {
                targetValue = Long.parseLong(val);
            } else if (value instanceof Integer val) {
                targetValue = Long.parseLong(String.valueOf(val));
            }
        } else if (BigDecimal.class.equals(returnType)) {
            if (value instanceof Number val) {
                targetValue = new BigDecimal(String.valueOf(val));
            } else if (value instanceof String val) {
                targetValue = new BigDecimal(val);
            }
        } else if (TypeEnum.class.isAssignableFrom(returnType)) {
            Class<TypeEnum<?>> typeEnumClass = (Class<TypeEnum<?>>) returnType;
            targetValue = TypeEnum.from(value, typeEnumClass);
            if (Objects.isNull(targetValue)) {
                if (value instanceof String val) {
                    try {
                        value = Integer.parseInt(val);
                    } catch (Exception e) {
                        // ignore
                    }
                } else if (value instanceof Integer val) {
                    value = String.valueOf(val);
                }
                targetValue = TypeEnum.from(value, typeEnumClass);
            }

        }

        if (value instanceof String val) {
            if (val.startsWith("{")) {
                targetValue = JsonUtil.toBean(val, returnType);
            } else if (val.startsWith("[")) {
                if (WrapperArrayList.class.isAssignableFrom(returnType)) {
                    targetValue = JsonUtil.toBean(val, returnType);
                } else {
                    targetValue = JsonUtil.toList(val, returnType);
                }
            }
        }
        return returnType.cast(targetValue);
    }

    public static <T> Tuple2<T, Map<String, Object>> autoConfig(T oldValue, Map<String, Object> newValue) {
        Map<String, Object> map = JsonUtil.beanToMap(oldValue);
        mergeMap(map, newValue);
        Object object = JsonUtil.mapToBean(map, oldValue.getClass());
        return new Tuple2<>((T) object, map);
    }

    private static void mergeMap(Map<String, Object> source, Map<String, Object> target) {
        if (Objects.isNull(source)) {
            return;
        }
        if (Objects.isNull(target)) {
            return;
        }
        for (Map.Entry<String, Object> entry : target.entrySet()) {
            String key = entry.getKey();
            Object newValue = entry.getValue();
            Object oldValue = source.get(key);
            if (Objects.isNull(oldValue) || Objects.isNull(newValue)) {
                source.put(key, newValue);
                continue;
            }
            if (oldValue instanceof Map<?, ?> oldM && newValue instanceof Map<?, ?> newM) {
                mergeMap((Map<String, Object>) oldM, (Map<String, Object>) newM);
                return;
            }
            source.put(key, newValue);
        }
    }
}
