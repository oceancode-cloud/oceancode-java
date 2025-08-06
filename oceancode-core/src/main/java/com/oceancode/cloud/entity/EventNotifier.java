package com.oceancode.cloud.entity;

import com.oceancode.cloud.common.util.SessionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EventNotifier {
    private Object newValue;
    private Object oldValue;
    private EventType eventType;
    private Long userId;
    private Set<Class<?>> classTypes = new HashSet<>();
    private List<String> tags = new ArrayList<>();
    private Map<String, Object> params = new HashMap<>();

    private EventNotifier() {
    }

    public EventNotifier withAdd() {
        return eventType(EventType.ADD);
    }

    public EventNotifier withAdd(Long id) {
        return withAdd().addParam("id", id);
    }

    public EventNotifier withDelete() {
        return eventType(EventType.DELETE);
    }

    public EventNotifier withDelete(Long id) {
        return withDelete().addParam("id", id);
    }


    public EventNotifier withUpdate() {
        return eventType(EventType.MODIFY);
    }

    public EventNotifier withUpdate(Long id) {
        return withUpdate().addParam("id", id);
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventNotifier newValue(Object value) {
        this.newValue = value;
        return this;
    }

    public EventNotifier oldValue(Object value) {
        this.oldValue = value;
        return this;
    }

    public EventNotifier eventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventNotifier addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
        return this;
    }

    public EventNotifier addTags(String... tags) {
        if (Objects.nonNull(tags)) {
            for (String tag : tags) {
                addTag(tag);
            }
        }
        return this;
    }

    public EventNotifier addTags(EventTag... tags) {
        if (Objects.nonNull(tags)) {
            for (EventTag tag : tags) {
                addTag(tag.getValue());
            }
        }
        return this;
    }

    public EventNotifier addParam(String key, Number value) {
        this.params.put(key, value);
        return this;
    }

    public EventNotifier addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public boolean isModify() {
        return EventType.MODIFY.equals(getEventType());
    }

    public boolean isAdd() {
        return EventType.ADD.equals(getEventType());
    }

    public boolean isDelete() {
        return EventType.DELETE.equals(getEventType());
    }

    public boolean isUpdate() {
        return isModify() || isAdd() || isDelete();
    }

    public EventNotifier userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        Object object = this.getParams().get("id");
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof Long) {
            return (Long) object;
        } else if (object instanceof String || object instanceof Integer) {
            return Long.parseLong(String.valueOf(object));
        }
        return null;
    }

    public EventNotifier addClassType(Class<?> classType) {
        this.classTypes.add(classType);
        return this;
    }

    public static EventNotifier classType(Class<?> classType) {
        EventNotifier eventNotifier = new EventNotifier();
        eventNotifier.addClassType(classType);
        eventNotifier.setEventType(EventType.ADD);
        eventNotifier.userId(SessionUtil.userId(false));
        return eventNotifier;
    }

    public boolean hasClassType(Class<?>... classes) {
        for (Class<?> aClass : classes) {
            for (Class<?> it : classTypes) {
                if (!aClass.equals(it) && !aClass.isAssignableFrom(it)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Long getParamAsLong(String key) {
        Object value = getParams().get(key);
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String str) {
            return Long.parseLong(str);
        } else if (value instanceof Integer v) {
            return Long.valueOf(v);
        }
        return null;
    }

    @Override
    public String toString() {
        return "EventNotifier{" +
                "eventType=" + eventType +
                ", tags=" + tags +
                ", params=" + params +
                '}';
    }
}
