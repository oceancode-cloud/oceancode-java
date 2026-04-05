package com.oceancode.cloud.common.util;

import java.util.List;
import java.util.Objects;

public final class ThreadUtil {
    private ThreadUtil() {
    }

    public static void runJob(String name, Runnable runnable) {
        runJob(name, null, runnable);
    }

    public static void runJob(String name, Integer index, Runnable runnable) {
        List<Object> values = SessionUtil.getValues();
        Thread.Builder.OfVirtual builder = Objects.nonNull(index) ? Thread.ofVirtual().name(name, index) : Thread.ofVirtual().name(name);
        Thread start = builder.start(() -> {
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
