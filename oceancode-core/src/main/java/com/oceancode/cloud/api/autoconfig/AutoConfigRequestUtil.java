package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class AutoConfigRequestUtil {
    private final static Map<String, Map<String, AutoConfigHandler>> AUTO_CONFIG_HANDLERS = new HashMap<>();
    private final static Set<AutoConfigRule> RULES = new HashSet<>();
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
        if (map.containsKey(property)) {
            AutoConfigHandler autoConfigHandler = map.get(property);
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "auto config handler(" + handler.getGroup() + "." + property + " duplicate," + handler.getClass().getName() + "," + autoConfigHandler.getClass().getName());
        }
        map.put(property, handler);
    }

    public static void registerRule(AutoConfigRule rule) {
        RULES.add(rule);
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

    public static List<AutoConfigRule> getRules(AutoConfigValue configValue) {
        return RULES.stream().filter(it -> it.support(configValue)).toList();
    }
}
