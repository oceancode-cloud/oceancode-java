package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.event.CommonEventStep;
import com.oceancode.cloud.api.event.EventStep;
import com.oceancode.cloud.common.event.EventImpl;
import com.oceancode.cloud.api.event.EventParam;
import com.oceancode.cloud.api.event.EventType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EventUtil {
    private static final Map<String, EventStep> EVENT_MAPPING = new ConcurrentHashMap<>();

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
        String id = SessionUtil.requestId() + ":" + type + ":";
        return id;
    }

    public static String createEventId(EventType type) {
        return createEventId(type.getId());
    }

    public static synchronized EventStep onEvent(EventType type, long timeout) {
        CommonEventStep commonEventStep = new CommonEventStep(timeout);
        onEvent(type, commonEventStep);
        return commonEventStep;
    }

    public static void onEvent(EventType type, EventStep eventStep) {
        onEvent(createEventId(type), eventStep);
    }

    public static void onEvent(String id, EventStep eventStep) {
        EVENT_MAPPING.put(id, eventStep);
    }

    public static void call(EventParam param) {
        call(createEventId(param.getEventType()), param);
    }

    public static void call(String id, EventParam param) {
        EventStep eventStep = EVENT_MAPPING.get(id);
        if (Objects.isNull(eventStep)) {
            return;
        }
        eventStep.setData(param);
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
        send(param, true);
    }

    public static void offEvent(EventType type) {
        offEvent(createEventId(type));
    }

    public static void offEvent(String id) {
        EVENT_MAPPING.remove(id);
    }
}
