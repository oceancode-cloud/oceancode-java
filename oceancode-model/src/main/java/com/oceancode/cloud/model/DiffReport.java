package com.oceancode.cloud.model;

import com.oceancode.cloud.api.ErrorCode;

import java.util.List;

public class DiffReport {
    private String key;
    private Object source;
    private Object target;
    private ErrorCode errorCode;
    private String message;
    private ValueStatus status;

    private List<DiffReport> children;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ValueStatus getStatus() {
        return status;
    }

    public void setStatus(ValueStatus status) {
        this.status = status;
    }

    public List<DiffReport> getChildren() {
        return children;
    }

    public void setChildren(List<DiffReport> children) {
        this.children = children;
    }
}
