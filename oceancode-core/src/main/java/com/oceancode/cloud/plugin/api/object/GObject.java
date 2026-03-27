package com.oceancode.cloud.plugin.api.object;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class GObject {
    private transient GContext context;
    private boolean isValidate;

    public Map<String, Object> get() {
        return Collections.emptyMap();
    }

    public Long sourceId() {
        validate();
        return propAsLong("id");
    }

    public String oid() {
        Object object = get().get("oid");
        if (Objects.isNull(object)) {
            return null;
        }
        return object.toString();
    }

    public Object prop(String key) {
        return get().get(key);
    }

    public String propAsString(String key, String defaultValue) {
        Object val = prop(key);
        if (Objects.isNull(val)) {
            return defaultValue;
        }
        String str = val.toString();
        if (ValueUtil.isEmpty(str)) {
            return defaultValue;
        }
        return str;
    }

    public String propAsString(String key) {
        return propAsString(key, null);
    }

    public Long propAsLong(String key, Long defaultValue) {
        Object val = prop(key);
        if (Objects.isNull(val)) {
            return defaultValue;
        } else if (val instanceof Long v) {
            return v;
        }
        String str = val.toString();
        if (ValueUtil.isEmpty(str)) {
            return defaultValue;
        }
        return Long.parseLong(str);
    }

    public Long propAsLong(String key) {
        return propAsLong(key, null);
    }

    public Integer propAsInteger(String key, Integer defaultValue) {
        Object val = prop(key);
        if (Objects.isNull(val)) {
            return defaultValue;
        } else if (val instanceof Integer v) {
            return v;
        }
        String str = val.toString();
        if (ValueUtil.isEmpty(str)) {
            return defaultValue;
        }
        return Integer.parseInt(str);
    }

    public Integer propAsInteger(String key) {
        return propAsInteger(key, null);
    }

    public Map<String, Object> propAsMap(String key, Map<String, Object> defaultValue) {
        Object val = prop(key);
        if (Objects.isNull(val)) {
            return defaultValue;
        } else if (val instanceof Map v) {
            return v;
        }
        return defaultValue;
    }

    public Map<String, Object> propAsMap(String key) {
        return propAsMap(key, Collections.emptyMap());
    }


    public String name() {
        return propAsString("name");
    }

    public String description() {
        return propAsString("description");
    }

    public Long groupId() {
        return propAsLong("groupId");
    }

    public GContext getContext() {
        return context;
    }

    public void setContext(GContext context) {
        this.context = context;
    }

    public String getGroup() {
        return ValueUtil.camelTo(this.getClass().getSimpleName(), "_");
    }

    public void validate() {
        if (isValidate) {
            return;
        }
        isValidate = true;
        doValidate();
    }

    protected void doValidate() {

    }
}
