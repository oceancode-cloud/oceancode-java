package com.oceancode.cloud.api.task;

public class TaskInfo {
    private String id;
    private TaskProperty properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskProperty getProperties() {
        return properties;
    }

    public void setProperties(TaskProperty properties) {
        this.properties = properties;
    }
}
