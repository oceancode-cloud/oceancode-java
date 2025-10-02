package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.event.EventImpl;
import com.oceancode.cloud.api.event.EventParam;
import com.oceancode.cloud.api.event.EventType;
import com.oceancode.cloud.common.event.EventWaitObj;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventUtil {
    private static final Map<String, EventWaitObj> EVENT_MAPPING = new ConcurrentHashMap<>();

    private EventUtil() {
    }

    public static void send(EventType type, Object data) {
        send(new EventParam(type.getId(), data), true);
    }

    public static void send(EventParam param, boolean isAsync) {
        if (isAsync) {
            ComponentUtil.getBean(EventImpl.class).sendAsync(param);
        } else {
            ComponentUtil.getBean(EventImpl.class).send(param);
        }
    }

    public static String createEventId(String type) {
        return type + ":" + SessionUtil.cursor();
    }

    public static String createEventId(EventType type) {
        return createEventId(type.getId());
    }

    public static EventParam onEvent(EventType type, long timeout) {
        return onEvent(createEventId(type), timeout);
    }

    public static EventParam onEvent(String id, long timeout) {
        if (EVENT_MAPPING.containsKey(id)) {
            return EVENT_MAPPING.get(id).getData();
        }
        EventWaitObj eventWaitObj = new EventWaitObj(timeout);
        EVENT_MAPPING.put(id, eventWaitObj);
        return eventWaitObj.getData();
    }

    public static void call(EventParam param) {
        call(createEventId(param.getEventType()), param);
    }

    public static void call(String id, EventParam param) {
        EventWaitObj eventWaitObj = EVENT_MAPPING.get(id);
        eventWaitObj.setData(param);
        if (!eventWaitObj.isValid()) {
            offEvent(id);
        }
    }

    public static void emitEvent(EventType type, String dataId) {
        emitEvent0(type, dataId);
    }

    public static void emitEvent(EventType type) {
        emitEvent0(type, null);
    }

    public static void emitEvent(EventType type, Long dataId) {
        emitEvent0(type, dataId);
    }

    public static void emitEvent(EventType type, Map<String, Object> map) {
        EventParam eventParam = new EventParam(type.getId(), map);
        emitEvent(eventParam);
    }

    private static void emitEvent0(EventType type, Object dataId) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", dataId);
        EventParam eventParam = new EventParam(type.getId(), map);
        emitEvent(eventParam);
    }

    public static void emitEvent(EventParam param) {
        send(param, false);
    }

    public static void offEvent(EventType type) {
        offEvent(createEventId(type));
    }

    public static void offEvent(String id) {
        EVENT_MAPPING.remove(id);
    }
}
