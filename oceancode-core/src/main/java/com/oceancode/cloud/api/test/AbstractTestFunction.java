package com.oceancode.cloud.api.test;

public abstract class AbstractTestFunction implements TestFunction {
    @Override
    public void execute(TestParam request, TestResult result) {
        doExecute(request, result);
    }

    protected abstract void doExecute(TestParam request, TestResult result);

    @Override
    public String getDescription() {
        return null;
    }
}
