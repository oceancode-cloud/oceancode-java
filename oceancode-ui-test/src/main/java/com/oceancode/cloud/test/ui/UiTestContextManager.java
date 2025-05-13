package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.test.TestContext;
import com.oceancode.cloud.test.TestContextManager;

public class UiTestContextManager extends TestContextManager {

    @Override
    public TestContext get(String caseId) {
        return map.computeIfAbsent(caseId, id -> new UiTestContext());
    }
}
