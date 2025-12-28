package com.oceancode.cloud.api.autoconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AutoConfigResultItem {
    private String versionId;
    private Map<String, Object> extra;
    private Boolean success;
    private String detail;
    private String group;
    private String dataId;
    private String rule;

    public static AutoConfigResultItem success() {
        AutoConfigResultItem item = new AutoConfigResultItem();
        item.setSuccess(true);
        return item;
    }

    public static AutoConfigResultItem of(boolean success) {
        return success ? success() : error();
    }

    public static AutoConfigResultItem error() {
        AutoConfigResultItem item = new AutoConfigResultItem();
        item.setSuccess(false);
        return item;
    }

    public AutoConfigResultItem detail(String detail) {
        this.detail = detail;
        return this;
    }

    public AutoConfigResultItem setExtra(String key, Object value) {
        if (Objects.isNull(extra)) {
            extra = new HashMap<>();
        }
        extra.put(key, value);
        return this;
    }

    public AutoConfigResultItem group(String group) {
        this.group = group;
        return this;
    }

    public AutoConfigResultItem dataId(Object dataId) {
        if (Objects.isNull(dataId)) {
            return this;
        }
        this.dataId = String.valueOf(dataId);
        return this;
    }

    public AutoConfigResultItem versionId(Object versionId) {
        if (Objects.isNull(versionId)) {
            return this;
        }
        this.versionId = String.valueOf(versionId);
        return this;
    }


    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
