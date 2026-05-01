package com.oceancode.cloud.api.tool;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolManager {
    public List<Map<String, Object>> getTools() {
        List<Map<String, Object>> list = new ArrayList<>();

        return list;
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
}
