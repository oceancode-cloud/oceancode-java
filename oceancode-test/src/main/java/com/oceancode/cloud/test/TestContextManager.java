package com.oceancode.cloud.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestContextManager {
    private Map<String, TestContext> map = new ConcurrentHashMap<>();

    public TestContext get(String caseId) {
        return map.computeIfAbsent(caseId, id -> new TestContext());
    }
}
