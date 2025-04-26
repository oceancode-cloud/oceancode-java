package com.oceancode.cloud.test.reporter;

import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.annotation.CaseId;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;


public class ReporterTestExecutionListener extends TestReporter implements BeforeEachCallback, AfterEachCallback, AfterAllCallback, AfterTestExecutionCallback {
    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        Collection<TestResult> results = getResults();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (!extensionContext.getTestMethod().isPresent()) {
            return;
        }
        Method method = extensionContext.getTestMethod().get();
        CaseId caseId = method.getAnnotation(CaseId.class);
        if (Objects.isNull(caseId)) {
            return;
        }
        TestResult testResult = getResultById(caseId.value());
        if (Objects.isNull(testResult)) {
            return;
        }
        if (Objects.isNull(testResult.getEndTime())) {
            testResult.setEndTime(System.nanoTime());
            testResult.setTotalTime(testResult.getEndTime() - testResult.getStartTime());
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        if (!extensionContext.getTestMethod().isPresent()) {
            return;
        }
        Method method = extensionContext.getTestMethod().get();
        CaseId caseId = method.getAnnotation(CaseId.class);
        if (Objects.isNull(caseId)) {
            return;
        }

        TestResult testResult = getResultById(caseId.value());
        if (extensionContext.getExecutionException().isPresent()) {
            testResult.setSuccess(false);
            testResult.setMessage(extensionContext.getExecutionException().get().getMessage());
            testResult.setThrowable(extensionContext.getExecutionException().get());

            if (testResult.getThrowable() instanceof ErrorCodeRuntimeException) {
                ErrorCodeRuntimeException exception = (ErrorCodeRuntimeException) testResult.getThrowable();

                testResult.setErrorCode(exception.getErrorCode());
            }
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if (!extensionContext.getTestMethod().isPresent()) {
            return;
        }
        Method method = extensionContext.getTestMethod().get();
        CaseId caseId = method.getAnnotation(CaseId.class);
        if (Objects.isNull(caseId)) {
            throw new RuntimeException("@CaseId is required.");
        }
        if (ValueUtil.isEmpty(caseId.value())) {
            throw new RuntimeException("caseId is required.");
        }
        TestResult testResult = new TestResult();
        testResult.setId(caseId.value());

        if (extensionContext.getTestClass().isPresent()) {
            testResult.setNamespace(extensionContext.getTestClass().get().getName());
        }
        if (extensionContext.getTestMethod().isPresent()) {
            testResult.setMethodName(method.getName());
        }

        testResult.setStartTime(System.nanoTime());
        testResult.setDescription(extensionContext.getDisplayName());
        addResult(testResult);
    }
}
