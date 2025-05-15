package com.oceancode.cloud.api.graph;

import java.util.Map;

public class GraphCell {
    public static final String NODE_NAME = "Node";
    public static final String EDGE_NAME = "r";
    private String id;
    private String name;
    private String type;
    private Map<String, Object> properties;

    public GraphCell(String id) {
        this.id = id;
    }

    public GraphCell() {
    }

    public boolean isNode() {
        return false;
    }

    public boolean isEdge() {
        return false;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "GraphCell{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                '}';
    }
}
