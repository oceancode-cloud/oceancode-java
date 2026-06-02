package com.oceancode.cloud.api.test;

public interface TestFunction {
    void execute(TestParam request, TestResult result);

    String getCaseId();

    String getName();

    String getDescription();
}
