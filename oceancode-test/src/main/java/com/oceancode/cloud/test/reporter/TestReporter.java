package com.oceancode.cloud.test.reporter;


import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestReporter {
    private final static Map<String, TestResult> results = new HashMap<>();

    protected void addTestResult(TestResult result) {
        String key = result.getNamespace() + ":" + result.getMethodName();
        results.put(key, result);
    }

    protected TestResult addTestResult(ExtensionContext context) {
        TestResult result = new TestResult();
        if (context.getTestClass().isPresent()) {
            result.setNamespace(context.getTestClass().get().getName());
        }
        if (context.getTestMethod().isPresent()) {
            result.setMethodName(context.getTestMethod().get().getName());
        }

        addTestResult(result);
        result.setStartTime(System.nanoTime());
        return result;
    }

    protected TestResult getTestResult(ExtensionContext context) {
        String key = context.getTestClass().get().getName() + ":" + context.getTestMethod().get().getName();
        return results.get(key);
    }

    public static Collection<TestResult> getResults() {
        return results.values();
    }
}
