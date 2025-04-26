package com.oceancode.cloud.test.reporter;

public class TestResult {
    private String namespace;
    private String methodName;
    private Long startTime;
    private Long endTime;
    private Long totalTime;
    private String description;
    private String message;
    private Throwable throwable;
    private boolean success;
    private String id;
    private Object response;
    private String errorCode;
    private Object inputs;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getInputs() {
        return inputs;
    }

    public void setInputs(Object inputs) {
        this.inputs = inputs;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "namespace='" + namespace + '\'' +
                ", methodName='" + methodName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalTime=" + totalTime +
                ", description='" + description + '\'' +
                ", message='" + message + '\'' +
                ", throwable=" + throwable +
                ", success=" + success +
                ", id='" + id + '\'' +
                ", response=" + response +
                '}';
    }
}
