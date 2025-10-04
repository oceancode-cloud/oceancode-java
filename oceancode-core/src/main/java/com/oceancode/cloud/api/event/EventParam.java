package com.oceancode.cloud.api.event;

import java.util.Map;
import java.util.Objects;

public class EventParam {
    private String eventType;
    private Object data;

    public EventParam(String eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getDataId() {
        if (data instanceof Map<?, ?> map) {
            return map.get("id");
        }
        return null;
    }

    public String getDataIdAsString() {
        Object id = getDataId();
        if (id instanceof String) {
            return (String) id;
        }
        if (Objects.isNull(id)) {
            return null;
        }
        return String.valueOf(id);
    }

    public Long getDataIdAsLong() {
        Object id = getDataId();
        if (id instanceof Long) {
            return (Long) id;
        } else if (id instanceof String str) {
            return Long.parseLong(str);
        } else if (id instanceof Integer) {
            return Long.parseLong(String.valueOf(id));
        }
        return null;
    }

    public boolean isSuccess() {
        if (data instanceof Map<?, ?> map) {
            Object object = map.get("success");
            if (object instanceof Boolean) {
                return (boolean) object;
            }
            return "true".equals(object);
        }
        return true;
    }

    public boolean isTypeOf(EventType eventType) {
        return Objects.equals(this.eventType, eventType.getId());
    }

    @Override
    public String toString() {
        return "EventParam{" +
                "eventType='" + eventType + '\'' +
                ", data=" + data +
                '}';
    }
}
