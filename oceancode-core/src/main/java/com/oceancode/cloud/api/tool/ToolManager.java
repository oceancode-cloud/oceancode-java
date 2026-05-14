package com.oceancode.cloud.api.tool;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ToolManager {
    private String name;

    public List<Map<String, Object>> getTools() {
        return Collections.emptyList();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object execute(String name, Map<String, Object> arguments) {
        return doExecute(name, new ToolParam(arguments));
    }

    protected Object doExecute(String name, ToolParam param) {
        return name;
    }

    public String getEventName(String method) {
        if (isClientMethod(method)) {
            return "func-call";
        }
        return "message";
    }

    public boolean isClientMethod(String method) {
        if (method.startsWith("_client.")) {
            return true;
        }
        return false;
    }

    public boolean hasOutputSchema(String name) {
        return true;
    }
}
