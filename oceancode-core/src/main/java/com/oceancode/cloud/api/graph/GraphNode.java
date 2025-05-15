package com.oceancode.cloud.api.graph;

import java.util.HashMap;

public class GraphNode extends GraphCell {

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

    @Override
    public boolean isNode() {
        return true;
    }
}
