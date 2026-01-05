package com.oceancode.cloud.api.autoconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoConfigResult {
    private List<AutoConfigResultItem> toAdd;
    private List<AutoConfigResultItem> toUpdate;
    private List<AutoConfigResultItem> toDelete;
    private Boolean success;
    private String detail;
    private transient Throwable throwable;
    private String errorCode;

    public AutoConfigResult toAdd(AutoConfigResultItem item) {
        if (Objects.isNull(toAdd)) {
            toAdd = new ArrayList<>();
        }
        toAdd.add(item);
        return this;
    }

    public AutoConfigResult toUpdate(AutoConfigResultItem item) {
        if (Objects.isNull(toUpdate)) {
            toUpdate = new ArrayList<>();
        }
        toUpdate.add(item);
        return this;
    }

    public AutoConfigResult toDelete(AutoConfigResultItem item) {
        if (Objects.isNull(toDelete)) {
            toDelete = new ArrayList<>();
        }
        toDelete.add(item);
        return this;
    }

    public List<AutoConfigResultItem> getToAdd() {
        return toAdd;
    }

    public void setToAdd(List<AutoConfigResultItem> toAdd) {
        this.toAdd = toAdd;
    }

    public List<AutoConfigResultItem> getToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(List<AutoConfigResultItem> toUpdate) {
        this.toUpdate = toUpdate;
    }

    public List<AutoConfigResultItem> getToDelete() {
        return toDelete;
    }

    public void setToDelete(List<AutoConfigResultItem> toDelete) {
        this.toDelete = toDelete;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
