package com.oceancode.cloud.api.test;

import java.util.HashMap;
import java.util.Map;

public class TestResult {
    private Map<String, Object> data;

    public TestResult(Map<String, Object> data) {
        this.data = data;
    }

    public TestResult() {
        this(new HashMap<>());
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
