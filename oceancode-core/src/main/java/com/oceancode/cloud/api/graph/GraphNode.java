package com.oceancode.cloud.api.graph;

import java.util.HashMap;

public class GraphNode extends GraphCell {
    private String group;

    private GraphNode() {
        setProperties(new HashMap<>());
    }

    public static GraphNode of(String id) {
        return new GraphNode().id(id);
    }

    public GraphNode type(String type) {
        this.setType(type);
        return this;
    }

    public GraphNode id(String id) {
        super.setId(id);
        return this;
    }

    public GraphNode name(String name) {
        super.setName(name);
        return this;
    }

    public GraphNode addProperty(String key, Object value) {
        getProperties().put(key, value);
        return this;
    }

    public GraphNode group(String group) {
        setGroup(group);
        return this;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean isNode() {
        return true;
    }
}
