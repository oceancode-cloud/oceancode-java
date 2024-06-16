/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import org.springframework.core.env.Environment;

import java.io.File;

public final class SystemUtil {
    public static void init() {
        File dataDirFile = new File(dataDir());
        if (!dataDirFile.exists()) {
            dataDirFile.mkdirs();
        }

        File tempDirFile = new File(tempDir());
        if (!tempDirFile.exists()) {
            tempDirFile.mkdirs();
        }
    }

    public static String dataDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String dataDir = environment.getProperty("os.system.data.dir");
        if (ValueUtil.isEmpty(dataDir)) {
            dataDir = System.getProperty("user.dir") + "/data";
        }

        return dataDir;
    }

    public static String tempDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String tempDir = environment.getProperty("app.system.temp.dir");
        if (ValueUtil.isEmpty(tempDir)) {
            return dataDir() + "/temp";
        }
        return tempDir;
    }

    public static boolean enableWeb() {
        String value = ComponentUtil.getBean(Environment.class).getProperty("oc.web.enable", "false");
        return Boolean.parseBoolean(value);
    }

    public static String htmlDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.system.web.html"));
    }

    public static String publicDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.system.web.resource.public"));
    }

    public static String privateResourceDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.system.web.resource.private"));
    }

    public static String privateResourceUrlPrefix() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String url = environment.getProperty("oc.system.web.resource.url.prefix", "/s/");
        return ValueUtil.isNotEmpty(url) ? (url.endsWith("/") ? url : url + "/") : null;
    }

    private static String parsePath(String path) {
        if (path == null) {
            return null;
        }
        if (path.startsWith(".")) {
            return System.getProperty("user.dir") + path;
        }
        return path;
    }

}