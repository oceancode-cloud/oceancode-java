package com.oceancode.cloud.api.tool;

public interface ToolFunction {
    String getName();
    String getDescription();
    ToolInputSchema getInputSchema();
}
