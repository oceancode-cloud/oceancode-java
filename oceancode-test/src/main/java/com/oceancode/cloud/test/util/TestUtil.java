package com.oceancode.cloud.test.util;

import cn.hutool.core.thread.ThreadUtil;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.data.Data;
import com.oceancode.cloud.test.data.TestData;
import com.oceancode.cloud.test.reporter.TestReporter;
import com.oceancode.cloud.test.reporter.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TestUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);

    private TestUtil() {
    }

    public static void performance(String caseId, int warmUpCount, List<Integer> countList, Runnable supplier) {
        if (ValueUtil.isEmpty(countList)) {
            return;
        }
        List<Integer> list = countList.stream().distinct().sorted().toList();
        TestResult result = TestReporter.getByCaseId(caseId);
        for (int i = 0; i < warmUpCount && warmUpCount > 0; i++) {
            supplier.run();
        }

        for (Integer count : list) {
            Long starTime = System.nanoTime();
            TestResult testResult = new TestResult();
            testResult.setGroup("thread");
            ThreadUtil.concurrencyTest(count, () -> {
                supplier.run();
            });
            testResult.setCaseId(caseId);
            testResult.setId(UUID.randomUUID().toString().replace("-", ""));
            testResult.setThreadId(Thread.currentThread().getId());
            testResult.setStartTime(starTime);
            testResult.setEndTime(System.nanoTime());
            testResult.setTotalTime(testResult.getEndTime() - testResult.getStartTime());
            testResult.setSaved(true);
            testResult.setParentId(result.getId());
            TestReporter.addResult(testResult);
        }
    }

    public static <T> T test(String caseId, Supplier<T> supplier) {
        return test(caseId, null, data -> supplier.get(), true);
    }

    public static <T, E> T test(String caseId, E data, Function<E, T> supplier) {
        return test(caseId, data, supplier, true);
    }

    public static <T, E> T test(String caseId, E data, Supplier<T> supplier) {
        return test(caseId, data, supplier, true);
    }

    public static <T, E> T test(String caseId, E data, Supplier<T> supplier, boolean throwEx) {
        return test(caseId, data, e -> supplier.get(), throwEx);
    }

    public static <T> T test(String caseId, Supplier<T> supplier, boolean throwEx) {
        return test(caseId, null, data -> supplier.get(), throwEx);
    }

    public static <T, E> T test(String caseId, E data, Function<E, T> supplier, boolean throwEx) {
        TestResult testResult = new TestResult();
        if (!TestReporter.exists(caseId)) {
            throw new RuntimeException(caseId + " is not same as the value of @CaseId,");
        }
        testResult.setStartTime(System.nanoTime());
        testResult.setSuccess(true);
        testResult.setCaseId(caseId);
        testResult.setId(UUID.randomUUID().toString().replace("-", ""));
        testResult.setParentId(TestReporter.getByCaseId(caseId).getId());
        testResult.setInputs(data);

        T resulst = null;
        try {
            resulst = supplier.apply(data);
            testResult.setEndTime(System.nanoTime());
            testResult.setResponse(resulst);
            testResult.setSuccess(true);
        } catch (Throwable throwable) {
            testResult.setEndTime(System.nanoTime());
            testResult.setThrowable(throwable);
            testResult.setMessage(throwable.getMessage());
            testResult.setSuccess(false);

            if (throwEx) {
                throw throwable;
            }

        } finally {
            testResult.setTotalTime(testResult.getEndTime() - testResult.getStartTime());
            testResult.setSaved(true);
            testResult.setGroup("detail");
            testResult.setThreadId(Thread.currentThread().getId());
        }

        return resulst;
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

    public static <T> List<TestData<T>> loadDatasets(String filePath, Class<T> returnType) {
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

    public static <T> TestData<T> loadDataset(String filePath, Class<T> returnType) {
        List<TestData<T>> list = loadDatasets(filePath, returnType);
        return list.isEmpty() ? null : list.get(0);
    }

    private static <T> List<TestData<T>> loadCsv(String filePath, Class<T> returnType) {
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
                            if (Objects.isNull(field)) {
                                field = "";
                            }
                            fields.add(field.trim());
                        }
                    }
                } else {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> dataMap = new HashMap<>();
                    map.put("data", dataMap);
                    for (int i = 0; i < fields.size(); i++) {
                        String value = i < cells.length ? cells[i] : null;
                        String field = fields.get(i);
                        if ("positive".equals((field + "").trim())) {
                            if ("true".equalsIgnoreCase((value + "").trim()) || "false".equalsIgnoreCase((value + "").trim())) {
                                map.put("positive", value);
                                continue;
                            }
                        }
                        dataMap.put(field, value);
                    }
                    if (!dataMap.isEmpty()) {
                        dataList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (dataList.isEmpty()) {
            return Collections.emptyList();
        }
        return (List<TestData<T>>) JsonUtil.toList(JsonUtil.toJson(dataList), TestData.class, returnType);
    }

    private static String getFilePath(String filePath) {
        if (Objects.isNull(filePath)) {
            return null;
        }
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        return ComponentUtil.getBean(CommonConfig.class).getValue("dataset.base.dir", SystemUtil.dataDir() + filePath);
    }

    private static <T> List<TestData<T>> loadJson(String filePath, Class<T> returnType) {
        try {
            String fileContent = new String(Files.readAllBytes(Paths.get(getFilePath(filePath))));
            if (!fileContent.trim().startsWith("[")) {
                fileContent = "[" + fileContent + "]";
            }
            return (List<TestData<T>>) JsonUtil.toList(fileContent, TestData.class, returnType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isDevelopEnv() {
        return new File(System.getProperty("user.dir") + File.separator + "target").exists();
    }
}
