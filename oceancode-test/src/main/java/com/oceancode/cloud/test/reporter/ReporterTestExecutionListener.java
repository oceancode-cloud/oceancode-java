package com.oceancode.cloud.test.reporter;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


public class ReporterTestExecutionListener extends TestReporter implements BeforeEachCallback, AfterEachCallback, AfterAllCallback, AfterTestExecutionCallback {
    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {

    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        TestResult testResult = getTestResult(extensionContext);
        testResult.setEndTime(System.nanoTime());
        testResult.setTotalTime(testResult.getEndTime() - testResult.getStartTime());
        testResult.setDescription(extensionContext.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        TestResult testResult = getTestResult(extensionContext);
        testResult.setSuccess(true);
        if (extensionContext.getExecutionException().isPresent()) {
            testResult.setSuccess(false);
            testResult.setMessage(extensionContext.getExecutionException().get().getMessage());
            testResult.setThrowable(extensionContext.getExecutionException().get());
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        TestResult result = addTestResult(extensionContext);
        result.setStartTime(System.nanoTime());
    }
}
