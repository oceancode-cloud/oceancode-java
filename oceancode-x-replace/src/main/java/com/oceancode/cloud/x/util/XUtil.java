package com.oceancode.cloud.x.util;

import com.oceancode.cloud.x.entity.Context;

import java.io.File;

public class XUtil {
    private final static ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    private XUtil() {
    }

    public static void init() {
        CONTEXT.set(new Context());
    }

    public static void remove() {
        CONTEXT.remove();
    }

    public static Context getContext() {
        return CONTEXT.get();
    }

}
