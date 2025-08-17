package com.oceancode.cloud.api.diff;

import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.List;

public class DiffDetail {
    private Object newVal;
    private Object oldVal;
    private ErrorCode errorCode;
    private String message;
    private ValueStatus status;
    private int updatedCount;
    private int deletedCount;
    private int addedCount;
    private boolean isCalcCount = false;

    private List<DiffDetail> children;

    public DiffDetail(Object newVal, Object oldVal, ValueStatus status) {
        this.newVal = newVal;
        this.oldVal = oldVal;
        this.status = status;
    }

    public Object getNewVal() {
        return newVal;
    }

    public void setNewVal(Object newVal) {
        this.newVal = newVal;
    }

    public Object getOldVal() {
        return oldVal;
    }

    public void setOldVal(Object oldVal) {
        this.oldVal = oldVal;
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

    public List<DiffDetail> getChildren() {
        return children;
    }

    public void setChildren(List<DiffDetail> children) {
        this.children = children;
    }

    public int updatedCount() {
        statistic();
        return updatedCount;
    }

    public int deletedCount() {
        statistic();
        return deletedCount;
    }

    public int addedCount() {
        statistic();
        return addedCount;
    }

    private void statistic() {
        if (isCalcCount) {
            return;
        }
        isCalcCount = true;
        if (ValueUtil.isEmpty(children)) {
            return;
        }
        for (DiffDetail child : children) {
            updatedCount += child.updatedCount();
            deletedCount += child.deletedCount();
            addedCount += child.addedCount();
        }
    }
}
