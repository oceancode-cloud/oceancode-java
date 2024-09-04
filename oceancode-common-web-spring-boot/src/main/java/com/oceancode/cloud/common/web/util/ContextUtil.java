package com.oceancode.cloud.common.web.util;

import com.oceancode.cloud.function.Context;

public final class ContextUtil {

    private final static ThreadLocal<Context> CONTEXT_LOCAL = new ThreadLocal<>();

    public static void set(Context context) {
        CONTEXT_LOCAL.set(context);
    }

    public static boolean isDsl() {
        return get() != null;
    }

    public static Context get() {
        return CONTEXT_LOCAL.get();
    }

    public static void remove() {
        CONTEXT_LOCAL.remove();
    }

    public static boolean hasQuerySelectionFields(String field, String... fields) {
        return isDsl() && get().hasSelectionFields(field, fields);
    }
}
