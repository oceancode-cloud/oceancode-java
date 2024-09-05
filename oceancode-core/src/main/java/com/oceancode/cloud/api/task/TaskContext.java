package com.oceancode.cloud.api.task;

public class TaskContext {
    private String id;
    private TaskParam param;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaskParam getParam() {
        return param;
    }

    public void setParam(TaskParam param) {
        this.param = param;
    }
}
