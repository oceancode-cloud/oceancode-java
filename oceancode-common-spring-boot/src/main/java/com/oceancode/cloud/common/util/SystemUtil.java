/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.Objects;

public final class SystemUtil {
    public static final String OUTPUT_DIR_CONFIG_KEY = "oc.system.output.dir";

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
            dataDir = System.getProperty("user.dir") + "/data";
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
            dataDir = System.getProperty("user.dir") + "/output";
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
            return System.getProperty("user.dir") + File.separator + path.trim();
        }
        return path.trim();
    }

    public static boolean killPort(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.close();
            return true;
        } catch (BindException bindException) {
            Integer processId = findProcessId("netstat -ano", port);
            if (Objects.isNull(processId)) {
                return false;
            }
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
        } catch (IOException e) {
            //ignore
        }
        return false;
    }


    private static Integer findProcessId(String locationCmd, int port) {
        try {
            Process child = Runtime.getRuntime().exec(locationCmd);
            InputStream in = child.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(":" + port + " ")) {
                    break;
                }
            }
            in.close();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                // ignore
            }
            String[] split = line.split(" ");
            if (split != null && split.length > 0) {
                return Integer.parseInt(split[split.length - 1]);
            }
        } catch (IOException e) {
            // ignore
        }
        return null;
    }
}