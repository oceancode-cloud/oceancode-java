package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.api.autoconfig.setter.BooleanSetter;
import com.oceancode.cloud.api.autoconfig.setter.CharArraySetter;
import com.oceancode.cloud.api.autoconfig.setter.LongSetter;
import com.oceancode.cloud.api.autoconfig.setter.StringTypeMapSetter;
import com.oceancode.cloud.api.autoconfig.setter.TypeSetter;
import com.oceancode.cloud.api.autoconfig.setter.StringListSetter;
import com.oceancode.cloud.api.autoconfig.setter.StringSetter;
import com.oceancode.cloud.api.autoconfig.setter.TypeEnumSetter;
import com.oceancode.cloud.api.autoconfig.setter.IntegerSetter;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.list.StringList;
import com.oceancode.cloud.common.util.TypeUtil;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AutoConfigRequestUtil {
    private final static Map<String, Map<String, AutoConfigHandler>> AUTO_CONFIG_HANDLERS = new HashMap<>();
    private final static String DEFAULT_PROPERTY = "*";

    public static void registerHandler(AutoConfigHandler handler) {
        if (ValueUtil.isEmpty(handler.getGroup())) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "auto config handler group is empty," + handler.getClass().getName());
        }
        Map<String, AutoConfigHandler> map = AUTO_CONFIG_HANDLERS.computeIfAbsent(handler.getGroup(), k -> new HashMap<>());
        String property = handler.getProperty();
        if (ValueUtil.isEmpty(property)) {
            property = DEFAULT_PROPERTY;
        }
        map.put(property, handler);
    }

    public static AutoConfigHandler getHandler(String group) {
        return getHandler(group, DEFAULT_PROPERTY);
    }

    public static AutoConfigHandler getHandler(String group, String property) {
        Map<String, AutoConfigHandler> map = AUTO_CONFIG_HANDLERS.get(group);
        if (Objects.isNull(map)) {
            return null;
        }
        return map.get(property);
    }

    public static List<AutoConfigHandler> getHandlers(String group) {
        return AUTO_CONFIG_HANDLERS.get(group).values().stream().toList();
    }

    private static boolean checkProperty(Map<String, Object> valueMap, String property) {
        if (Objects.isNull(valueMap)) {
            return false;
        }
        if (ValueUtil.isEmpty(property)) {
            return false;
        }
        if (!valueMap.containsKey(property)) {
            return false;
        }
        return true;
    }

    public static void setPropValue(StringSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        String value = null;
        if (rawValue instanceof String str) {
            value = str;
        } else if (Objects.nonNull(rawValue)) {
            value = rawValue.toString();
        }
        setter.set(value);
    }

    public static void setPropValue(BooleanSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        Boolean value = null;
        if (rawValue instanceof Boolean val) {
            value = val;
        } else if (rawValue instanceof String str) {
            value = Boolean.parseBoolean(str);
        } else if (Objects.nonNull(rawValue)) {
            return;
        }
        setter.set(value);
    }

    public static void setPropTypeEnumValue(TypeEnumSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        Object value = null;
        boolean ret = Objects.isNull(rawValue) ||
                rawValue instanceof String ||
                rawValue instanceof Integer;
        if (!ret) {
            return;
        }
        setter.set(value);
    }

    public static void setPropValue(StringListSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        if (Objects.isNull(rawValue)) {
            setter.set(null);
            return;
        }
        StringList value = new StringList();
        if (rawValue instanceof Collection val) {
            value.addAll(val);
        } else {
            return;
        }
        setter.set(value);
    }

    public static void setPropValue(IntegerSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        if (Objects.isNull(rawValue)) {
            setter.set(null);
            return;
        }
        setter.set(TypeUtil.convertToInteger(rawValue));
    }

    public static void setPropValue(LongSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        if (Objects.isNull(rawValue)) {
            setter.set(null);
            return;
        }
        setter.set(TypeUtil.convertToLong(rawValue));
    }

    public static <T> void setPropTypeValue(TypeSetter<T> setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        if (Objects.isNull(rawValue)) {
            setter.set(null);
            return;
        }
        setter.set((T) rawValue);
    }

    public static void setPropValue(CharArraySetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        if (Objects.isNull(rawValue)) {
            setter.set(null);
            return;
        }
        if (rawValue instanceof String str) {
            setter.set(str.toCharArray());
            return;
        }
        setter.set((char[]) rawValue);
    }

    public static void setPropValue(StringTypeMapSetter setter, Map<String, Object> valueMap, String property) {
        if (!checkProperty(valueMap, property)) {
            return;
        }
        Object rawValue = valueMap.get(property);
        if (Objects.isNull(rawValue)) {
            setter.set(null);
            return;
        }
        if (rawValue instanceof Map map) {
            setter.set(map);
        }
    }
}
