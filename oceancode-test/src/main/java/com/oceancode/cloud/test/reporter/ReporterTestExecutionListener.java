package com.oceancode.cloud.test.reporter;

import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.FileUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.SystemUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.annotation.CaseId;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;


public class ReporterTestExecutionListener extends TestReporter implements BeforeEachCallback, AfterEachCallback, AfterAllCallback, AfterTestExecutionCallback {
    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        Collection<TestResult> results = getResults();
        String filename = extensionContext.getTestClass().get().getName() + "." + System.currentTimeMillis() + ".json";
        FileUtil.writeStringToFile(new File(SystemUtil.outputDir() + "/" + filename), JsonUtil.toJson(results));
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
        TestResult testResult = getByCaseId(caseId.value());
        if (Objects.isNull(testResult)) {
            return;
        }
        testResult.setEndTime(System.nanoTime());
        testResult.setTotalTime(testResult.getEndTime() - testResult.getStartTime());
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

        TestResult testResult = getByCaseId(caseId.value());
        if (Objects.isNull(testResult)) {
            return;
        }
        if (extensionContext.getExecutionException().isPresent()) {
            testResult.setSuccess(false);
            testResult.setMessage(extensionContext.getExecutionException().get().getMessage());
            testResult.setThrowable(extensionContext.getExecutionException().get());

            if (testResult.getThrowable() instanceof ErrorCodeRuntimeException) {
                ErrorCodeRuntimeException exception = (ErrorCodeRuntimeException) testResult.getThrowable();

                testResult.setErrorCode(exception.getErrorCode());
            }
        }

        testResult.setEndTime(System.nanoTime());
        testResult.setTotalTime(testResult.getEndTime() - testResult.getStartTime());

        processTestResult(testResult);
    }

    private void processTestResult(TestResult testResult) {
        if (Objects.nonNull(testResult.getThrowable())) {
            Throwable throwable = testResult.getThrowable();
            if (throwable instanceof AssertionFailedError) {
                AssertionFailedError assertionFailedError = (AssertionFailedError) throwable;
                testResult.setExpected(assertionFailedError.getExpected());
                testResult.setActual(assertionFailedError.getActual());
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
        if (exists(caseId.value())) {
            throw new RuntimeException("caseId[" + caseId.value() + " already exsists.");
        }
        TestResult testResult = new TestResult();
        testResult.setCaseId(caseId.value());
        testResult.setId(UUID.randomUUID().toString().replace("-", ""));

        if (extensionContext.getTestClass().isPresent()) {
            testResult.setNamespace(extensionContext.getTestClass().get().getName());
        }
        if (extensionContext.getTestMethod().isPresent()) {
            testResult.setMethodName(method.getName());
        }

        testResult.setStartTime(System.nanoTime());
        testResult.setGroup("method");
        testResult.setThreadId(Thread.currentThread().getId());
        testResult.setDescription(extensionContext.getDisplayName());
        addResult(testResult);
    }
}
