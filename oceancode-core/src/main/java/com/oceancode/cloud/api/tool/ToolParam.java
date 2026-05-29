package com.oceancode.cloud.api.tool;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ToolParam {
    private Map<String, Object> map;

    public ToolParam(Map<String, Object> map) {
        this.map = map;
        if (Objects.isNull(map)) {
            this.map = Collections.emptyMap();
        }
    }

    public Map<String, Object> params() {
        return map;
    }

    public Object param(String key) {
        return map.get(key);
    }

    public String asText(String key) {
        Object param = param(key);
        if (Objects.isNull(param)) {
            return null;
        }
        return param.toString();
    }

    public String asString(String key) {
        return asText(key);
    }

    public Long asLong(String key) {
        String text = asText(key);
        if (ValueUtil.isEmpty(text)) {
            return null;
        }
        return Long.parseLong(text);
    }

    public Integer asInteger(String key) {
        String text = asText(key);
        if (ValueUtil.isEmpty(text)) {
            return null;
        }
        return Integer.parseInt(text);
    }

    public Boolean asBoolean(String key) {
        String text = asText(key);
        if (ValueUtil.isEmpty(text)) {
            return null;
        }
        if ("0".equals(text)) {
            return false;
        } else if ("1".equals(text)) {
            return true;
        }
        return Boolean.parseBoolean(text);
    }

    public Map<String, Object> asMap(String key) {
        Object param = param(key);
        if (param instanceof Map map) {
            return map;
        }
        return Collections.emptyMap();
    }

    public <T> List<T> asList(String key, Class<T> type) {
        Object param = param(key);
        if (!(param instanceof List<?> list)) {
            return Collections.emptyList();
        }
        return list.stream().filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public List<String> asStringList(String key) {
        return asList(key, String.class);
    }

    public List<Integer> asIntegerList(String key) {
        return asList(key, Integer.class);
    }

    public List<Long> asLongList(String key) {
        return asList(key, Long.class);
    }

    public List<Object> asList(String key) {
        Object param = param(key);
        if (param instanceof List list) {
            return list;
        }
        return Collections.emptyList();
    }

}
