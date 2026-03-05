package com.oceancode.cloud.api.autoconfig.v2;

import java.util.List;
import java.util.Map;

public class AutoConfigResult {
    private boolean success = true;
    private String group;
    private String property;
    private List<AutoConfigResult> children;
    private String notifier;
    private String parent;
    private Map<String, Object> extra;
    private String newValue;
    private String oldValue;
    private String versionId;
    private String errorCode;
    private String errorMessage;
    private String sourceId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<AutoConfigResult> getChildren() {
        return children;
    }

    public void setChildren(List<AutoConfigResult> children) {
        this.children = children;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
