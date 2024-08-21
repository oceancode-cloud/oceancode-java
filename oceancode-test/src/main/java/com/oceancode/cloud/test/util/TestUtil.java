package com.oceancode.cloud.test.util;

import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.test.data.Data;

import java.util.function.Supplier;

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

    public static void withData(Data data, Runnable runnable, boolean ignoreBusinesses) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (data.isPositive()) {
                if (!(ignoreBusinesses && throwable instanceof ErrorCodeRuntimeException)) {
                    throw throwable;
                }
            }
        }
    }

    public static void smokeWithData(Data data, Runnable runnable) {
        if (!data.isPositive()) {
            return;
        }
        runnable.run();
        data.release();
    }
}
