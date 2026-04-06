package com.oceancode.cloud.plugin.api.object;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GObject implements Identifier {
    private transient GContext context;
    private boolean isValidate;
    private transient Map<String, Object> extraMap;

    public Map<String, Object> get() {
        return Collections.emptyMap();
    }

    public boolean isValidate() {
        return isValidate;
    }

    public Long sourceId() {
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

    public void save() {

    }

    public void save(Runnable runnable) {
        runnable.run();
    }

    public void delete() {

    }

    public void oSetExtra(String key, Object value) {
        if (Objects.isNull(extraMap)) {
            extraMap = new HashMap<>();
        }
        extraMap.put(key, value);
    }

    public Object oGetExtra(String key) {
        if (Objects.isNull(extraMap)) {
            return null;
        }
        return extraMap.get(key);
    }

    public Map<String, Object> oGetAllExtra() {
        if (Objects.isNull(extraMap)) {
            return Collections.emptyMap();
        }
        return extraMap;
    }

    public void setProperty(String key, Object value) {

    }

    public void setProperties(Map<String, Object> values) {

    }

    public void reload() {

    }

    @Override
    public void oSetId(String id) {
        setProperty("id", id);
    }

    @Override
    public String oGetId() {
        Long id = sourceId();
        if (Objects.isNull(id)) {
            return null;
        }
        return id.toString();
    }

    public Object getInfo() {
        return null;
    }
}
