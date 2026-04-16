package com.oceancode.cloud.autoconfig;

import java.util.ArrayList;
import java.util.List;

public class AutoConfigResponse {
    private Boolean success = true;
    private List<AutoConfigResult> toAdd = new ArrayList<>();
    private List<AutoConfigResult> toUpdate = new ArrayList<>();
    private List<AutoConfigResult> toDelete = new ArrayList<>();
    private String errorMessage;
    private String errorCode;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void toAdd(AutoConfigResult result) {
        toAdd.add(result);
    }

    public void toUpdate(AutoConfigResult result) {
        toUpdate.add(result);
    }

    public void toDelete(AutoConfigResult result) {
        toDelete.add(result);
    }

    public List<AutoConfigResult> getToAdd() {
        return toAdd;
    }

    public void setToAdd(List<AutoConfigResult> toAdd) {
        this.toAdd = toAdd;
    }

    public List<AutoConfigResult> getToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(List<AutoConfigResult> toUpdate) {
        this.toUpdate = toUpdate;
    }

    public List<AutoConfigResult> getToDelete() {
        return toDelete;
    }

    public void setToDelete(List<AutoConfigResult> toDelete) {
        this.toDelete = toDelete;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
