package com.oceancode.cloud.tool;

import com.oceancode.cloud.api.tool.ToolFunction;
import com.oceancode.cloud.api.tool.ToolInputSchema;
import com.oceancode.cloud.common.util.JsonUtil;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.DefaultToolDefinition;
import org.springframework.ai.tool.definition.ToolDefinition;

public abstract class AbstractToolFunction implements ToolFunction, ToolCallback {
    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ToolInputSchema getInputSchema() {
        return null;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        ToolDefinition definition = new DefaultToolDefinition(getName(), getDescription(), JsonUtil.toJson(getInputSchema()));
        return definition;
    }
}
