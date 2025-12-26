/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class SystemUtil {
    public static final String OUTPUT_DIR_CONFIG_KEY = "oc.system.output.dir";

    private static String getAppBinWorkDir() {
        return getAppBinWorkFile().getAbsolutePath() + File.separator;
    }

    private static File getAppBinWorkFile() {
        String dir = System.getProperty("user.dir");
        File file = new File(dir, "bin/startup.sh");
        if (file.exists()) {
            file = file.getParentFile().getParentFile();
        }
        return file;
    }

    public static boolean isWindow() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

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
        String dataDir = environment.getProperty("oc.system.data.dir");
        if (ValueUtil.isEmpty(dataDir)) {
            dataDir = getAppBinWorkDir() + "data";
        }

        return dataDir;
    }

    public static String repositoryDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String dataDir = environment.getProperty("oc.system.repository.dir");
        if (ValueUtil.isEmpty(dataDir)) {
            dataDir = dataDir();
        }

        return dataDir + "/repository";
    }

    public static String outputDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String dataDir = environment.getProperty(OUTPUT_DIR_CONFIG_KEY);
        if (ValueUtil.isEmpty(dataDir)) {
            dataDir = getAppBinWorkDir() + "output";
        }

        return dataDir;
    }

    public static String tempDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String tempDir = environment.getProperty("oc.tmp.dir", "../data/tmp");
        if (ValueUtil.isEmpty(tempDir)) {
            try {
                return Path.of(dataDir(), "../data/tmp").toFile().getCanonicalPath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (tempDir.startsWith("../")) {
            try {
                return Path.of(dataDir(), tempDir).toFile().getCanonicalPath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tempDir;
    }

    public static boolean enableWeb() {
        String value = ComponentUtil.getBean(Environment.class).getProperty("oc.web.enabled", "false");
        return Boolean.parseBoolean(value);
    }

    public static String htmlDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.web.html", "../data/web/html"));
    }

    public static String publicDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.web.resource.public", "../data/web/public"));
    }

    public static String pluginDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.plugin.dir", "../plugins"));
    }

    public static String privateResourceDir() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        return parsePath(environment.getProperty("oc.web.resource.private", "../data/web/private"));
    }

    public static String privateResourceUrlPrefix() {
        Environment environment = ComponentUtil.getBean(Environment.class);
        String url = environment.getProperty("oc.web.resource.private.url.prefix", "/s/");
        return ValueUtil.isNotEmpty(url) ? (url.endsWith("/") ? url : url + "/") : null;
    }

    public static String parsePath(String path) {
        if (path == null) {
            return null;
        }
        if (path.trim().startsWith(".")) {
            return getAppBinWorkDir() + path.trim();
        }
        return path.trim();
    }

    public static boolean killProcess(int processId) {
        try {
            String cmd = "kill -15 " + processId;
            if (isWindow()) {
                cmd = "taskkill /PID " + processId + " /F";
            }
            Process killProcess = Runtime.getRuntime().exec(cmd);
            return killProcess.waitFor() == 0;
        } catch (Exception e) {
            //ignore
        }
        return false;
    }

    public static List<String> execCommand(String command, Function<String, Boolean> function) {
        List<String> list = new ArrayList<>();
        try {
            Process child = Runtime.getRuntime().exec(command);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (Objects.nonNull(function)) {
                    if (!ValueUtil.isTrue(function.apply(line))) {
                        continue;
                    }
                }
                list.add(line);
            }
            in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                // ignore
            }
        } catch (IOException e) {
            // ignore
        }
        return list;
    }

    public static void deleteDirectory(File directory) {
        if (Objects.isNull(directory)) {
            return;
        }
        if (!directory.isDirectory()) {
            return;
        }
        if (!directory.exists()) {
            return;
        }
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}