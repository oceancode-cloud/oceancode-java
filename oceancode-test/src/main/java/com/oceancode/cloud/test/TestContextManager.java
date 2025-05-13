package com.oceancode.cloud.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestContextManager {
    protected Map<String, TestContext> map = new ConcurrentHashMap<>();

    public TestContext get(String caseId) {
        return map.computeIfAbsent(caseId, id -> new TestContext());
    }

    public TestContext get() {
        return get("default");
    }
}
