package com.oceancode.cloud.test.reporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestReporter {
    private final static List<TestResult> results = Collections.synchronizedList(new ArrayList<>());
    private final static Set<String> ids = Collections.synchronizedSet(new HashSet<>());


    public static void addResult(TestResult testResult) {
        ids.add(testResult.getCaseId());
        results.add(testResult);
    }

    public static boolean exists(String caseId) {
        return ids.contains(caseId);
    }

    public static List<TestResult> getResults() {
        return results;
    }

    public static TestResult getByCaseId(String caseId) {
        return getResults().stream().filter(e -> e.getCaseId().equals(caseId) && "method".equals(e.getGroup()))
                .findFirst().orElse(null);
    }
}
