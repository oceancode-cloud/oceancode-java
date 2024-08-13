package com.oceancode.cloud.test.util;

import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;

public final class TestUtil {
    private TestUtil() {
    }

    public static void fuzz(int maxCount, Runnable runnable) {
        for (int i = 0; i < maxCount; i++) {
            try {
                runnable.run();
            } catch (Throwable e) {
                if (!(e instanceof ErrorCodeRuntimeException)) {
                    throw e;
                }
            }
        }
    }

    public static void fuzz(Runnable runnable) {
        fuzz(100000, runnable);
    }
}
