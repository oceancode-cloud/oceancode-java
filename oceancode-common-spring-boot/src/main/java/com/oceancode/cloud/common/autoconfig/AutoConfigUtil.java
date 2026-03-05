package com.oceancode.cloud.common.autoconfig;

import com.oceancode.cloud.api.autoconfig.AutoConfigType;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class AutoConfigUtil {
    private AutoConfigUtil() {
    }

    public static List<AutoConfig> filterType(Collection<AutoConfig> collection, AutoConfigType type) {
        return collection
                .stream().filter(it -> Objects.equals(type.getValue(), it.getType()))
                .toList();
    }

    public static Map<String, Object> filterTypeToMap(Collection<AutoConfig> collection, AutoConfigType type) {
        Map<String, Object> map = new HashMap<>();
        collection
                .stream().filter(it -> Objects.equals(type.getValue(), it.getType()))
                .forEach(it -> {
                    map.put(it.getProperty(), it.getNewValue());
                    if (ValueUtil.isNotEmpty(it.getVersionId())) {
                        map.put("versionId", it.getVersionId());
                    }
                });
        return map;
    }

    public static <T> Optional<T> filterUpdateTo(Collection<AutoConfig> collection, Class<T> beanType) {
        Map<String, Object> map = filterTypeToMap(collection, AutoConfigType.UPDATE);
        if (map.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(JsonUtil.mapToBean(map, beanType));
    }

    public static List<AutoConfig> filterUpdate(Collection<AutoConfig> collection) {
        return filterType(collection, AutoConfigType.UPDATE);
    }
}
