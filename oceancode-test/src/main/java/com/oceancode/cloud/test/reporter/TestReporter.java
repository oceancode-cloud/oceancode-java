package com.oceancode.cloud.test.reporter;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TestReporter {
    private final static Map<String, TestResult> results = new ConcurrentHashMap<>();


    public static void addResult(TestResult testResult) {
        if (Objects.nonNull(results.get(testResult.getId()))) {
            throw new RuntimeException(testResult.getId() + " case already exists.");
        }
        results.put(testResult.getId(), testResult);
    }

    public static TestResult getResultById(String caseId) {
        return results.get(caseId);
    }

    public static Collection<TestResult> getResults() {
        return results.values();
    }
}
