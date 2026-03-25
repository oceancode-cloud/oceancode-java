package com.oceancode.cloud.common.util;

import java.util.List;
import java.util.Map;

public final class ThreadUtil {
    private ThreadUtil() {
    }

    public static void runJob(String name, Runnable runnable) {
        List<Object> values = SessionUtil.getValues();
        Thread start = Thread.ofVirtual().name(name).start(() -> {
            SessionUtil.setValues(values);
            try {
                runnable.run();
            } finally {
                SessionUtil.remove();
            }
        });
        start.run();
    }
}
