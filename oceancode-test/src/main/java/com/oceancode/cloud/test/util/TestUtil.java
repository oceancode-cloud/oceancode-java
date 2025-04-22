package com.oceancode.cloud.test.util;

import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.data.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public static <T> List<T> loadDatasets(String filePath, Class<T> returnType) {
        if (ValueUtil.isEmpty(filePath)) {
            return Collections.emptyList();
        }
        String path = filePath.trim();
        if (path.endsWith(".json")) {
            return loadJson(filePath, returnType);
        } else if (path.endsWith(".csv")) {
            return loadCsv(filePath, returnType);
        }
        return Collections.emptyList();
    }

    public static <T> T loadDataset(String filePath, Class<T> returnType) {
        List<T> list = loadDatasets(filePath, returnType);
        return list.isEmpty() ? null : list.get(0);
    }

    private static <T> List<T> loadCsv(String filePath, Class<T> returnType) {
        if (ValueUtil.isEmpty(filePath)) {
            return Collections.emptyList();
        }
        String line = "";
        String splitBy = ",";
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(getFilePath(filePath)))) {
            while ((line = br.readLine()) != null) {
                String[] cells = line.split(splitBy);
                if (fields.isEmpty()) {
                    if (ValueUtil.isNotEmpty(line) && cells.length > 0) {
                        for (String cell : cells) {
                            String field = cell;
                            if (Objects.nonNull(field)) {
                                if (field.contains("(") && field.contains(")") && field.indexOf("(") < field.indexOf(")")) {
                                    field = field.substring(field.indexOf("(") + 1, field.indexOf(")"));
                                }
                            }
                            fields.add(field);
                        }
                    }
                } else {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < fields.size(); i++) {
                        String value = i < cells.length - 1 ? cells[i] : null;
                        map.put(fields.get(i), value);
                    }
                    if (!map.isEmpty()) {
                        dataList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        if (dataList.isEmpty()) {
            return Collections.emptyList();
        }
        return JsonUtil.toList(JsonUtil.toJson(dataList), returnType);
    }

    private static String getFilePath(String filePath) {
        if (!(filePath.startsWith("/") || filePath.startsWith(File.separator))) {
            filePath = File.separator + filePath;
        }
        return ComponentUtil.getBean(CommonConfig.class).getValue("dataset.base.dir", SystemUtil.dataDir() + filePath);
    }

    private static <T> List<T> loadJson(String filePath, Class<T> returnType) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(getFilePath(filePath))));
            if (!fileContent.trim().startsWith("[")) {
                fileContent = "[" + fileContent + "]";
            }
            return JsonUtil.toList(fileContent, returnType);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
