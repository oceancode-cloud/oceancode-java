package com.oceancode.cloud.api.tool;

import java.util.Map;
import java.util.Set;

public class ToolInputSchema {
    private String type;
    private Map<String, ToolVarSchema> properties;
    private Set<String> requiredFields;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, ToolVarSchema> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ToolVarSchema> properties) {
        this.properties = properties;
    }

    public Set<String> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(Set<String> requiredFields) {
        this.requiredFields = requiredFields;
    }
}
