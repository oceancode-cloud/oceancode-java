package com.oceancode.cloud.api.autoconfig;

import java.util.Map;

public class AutoConfigRequestItem {
    private String group;
    private String notifier;
    private String property;
    private String type;
    private String oldValue;
    private String newValue;
    private Map<String, Object> extra;
    private Long timestamp;
    private String versionId;

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }



    @Override
    public String toString() {
        return "AutoConfigRequestItem{" +
                "group='" + group + '\'' +
                ", notifier='" + notifier + '\'' +
                ", property='" + property + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
