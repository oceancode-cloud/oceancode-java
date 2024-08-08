package com.oceancode.cloud.reporter;

import com.oceancode.cloud.common.util.FileUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestReporter implements IReporter {
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        List<ITestResult> list = new ArrayList<>();
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> suiteResults = suite.getResults();
            for (ISuiteResult suiteResult : suiteResults.values()) {
                ITestContext testContext = suiteResult.getTestContext();
                IResultMap passedTests = testContext.getPassedTests();
                IResultMap failedTests = testContext.getFailedTests();
                IResultMap skippedTests = testContext.getSkippedTests();
                IResultMap failedConfig = testContext.getFailedConfigurations();
                list.addAll(this.listTestResult(passedTests));
                list.addAll(this.listTestResult(failedTests));
                list.addAll(this.listTestResult(skippedTests));
                list.addAll(this.listTestResult(failedConfig));
            }
        }
        this.sort(list);
        this.outputResult(list, outputDirectory + "/smoke-repository-reporter.json");
    }

    private void outputResult(List<ITestResult> list, String outputDir) {
        List<Map<String, Object>> outList = new ArrayList<>();
        for (ITestResult result : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("className", result.getTestClass().getRealClass().getName());
            map.put("methodName", result.getMethod().getMethodName());
            map.put("startTime", this.formatDate(result.getStartMillis()));
            map.put("endTime", this.formatDate(result.getEndMillis()));
            map.put("used", result.getEndMillis() - result.getStartMillis());
            map.put("status", result.getStatus());
            map.put("statusDesc", this.getStatus(result.getStatus()));
            map.put("description", result.getMethod().getDescription());
            map.put("groups", result.getMethod().getGroups());

            outList.add(map);
        }

        writerReporter(outputDir, outList);
    }

    protected void writerReporter(String outputDir, List<Map<String, Object>> outList) {
        FileUtil.writeStringToFile(new File(outputDir), JsonUtil.toJson(outList));
    }

    private Object getStatus(int status) {
        switch (status) {
            case 1:
                return "SUCCESS";
            case 2:
                return "FAILURE";
            case 3:
                return "SKIP";
        }
        return null;
    }

    private Object formatDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(date);
    }

    private void sort(List<ITestResult> list) {
        Collections.sort(list, new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                if (o1.getStartMillis() > o2.getStartMillis()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    private ArrayList<ITestResult> listTestResult(IResultMap resultMap) {
        Set<ITestResult> results = resultMap.getAllResults();
        return new ArrayList<>(results);
    }
}
