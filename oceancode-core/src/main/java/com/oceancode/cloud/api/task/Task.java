package com.oceancode.cloud.api.task;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private String name;
    private String description;
    private TaskParam param;
    private String express;
    private ExpressType expressType;

    private String handler;
    private ExecuteType executeType;
    private TaskProperty properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskParam getParam() {
        return param;
    }

    public void setParam(TaskParam param) {
        this.param = param;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public ExpressType getExpressType() {
        return expressType;
    }

    public void setExpressType(ExpressType expressType) {
        this.expressType = expressType;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public ExecuteType getExecuteType() {
        return executeType;
    }

    public void setExecuteType(ExecuteType executeType) {
        this.executeType = executeType;
    }

    public TaskProperty getProperties() {
        return properties;
    }

    public void setProperties(TaskProperty properties) {
        this.properties = properties;
    }
}
