package com.oceancode.cloud.test.base;

import com.oceancode.cloud.api.plugin.ApiMetricsPlugin;
import com.oceancode.cloud.api.plugin.Metrics;
import com.oceancode.cloud.test.reporter.TestReporter;
import com.oceancode.cloud.test.reporter.TestResult;
import jakarta.annotation.Resource;

import java.util.List;

public class BaseApiPerformanceTest extends BaseApiTest {
    @Resource
    private ApiMetricsPlugin apiMetricsPlugin;

    protected List<Metrics> test(String caseId, String method, Runnable runnable) {
        return test(caseId, true, method, runnable);
    }

    protected List<Metrics> test(String caseId, boolean output, String method, Runnable runnable) {
        if (!TestReporter.exists(caseId)) {
            throw new RuntimeException("caseId[" + caseId + "] not found.");
        }
        List<Metrics> metrics = apiMetricsPlugin.getMetrics(getUrl(), method, runnable);
        List<TestResult> list = metrics.stream().map(e -> {
            TestResult testResult = new TestResult();
            testResult.setId(e.getId());
            testResult.setParentId(e.getParentId());
            testResult.setMethodName(e.getName());
            testResult.setNamespace(e.getPath());
            testResult.setTotalTime(e.getTotalCost());
            testResult.setStartTime(e.getStartTime());
            testResult.setCaseId(caseId);
            testResult.setEndTime(e.getEndTime());
            testResult.setLineNumber(e.getCodeLineNumber());
            if (output) {
                TestReporter.addResult(testResult);
            }
            return testResult;
        }).toList();
        return metrics;
    }

    protected String getUrl() {
        return null;
    }
}
