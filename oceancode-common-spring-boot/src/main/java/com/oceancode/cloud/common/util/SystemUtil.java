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
        String dataDir = environment.getProperty("app.system.data.dir");
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
        String value = ComponentUtil.getBean(Environment.class).getProperty("app.web.enable", "false");
        return Boolean.parseBoolean(value);
    }

    public static String htmlDir() {
        return dataDir() + "/web/html";
    }

    public static String publicDir() {
        return dataDir() + "/web/public";
    }
}