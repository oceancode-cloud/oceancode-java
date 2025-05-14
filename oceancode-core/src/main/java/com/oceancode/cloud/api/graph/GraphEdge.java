package com.oceancode.cloud.api.graph;

public class GraphEdge extends GraphCell {

    private String sourceId;
    private String targetId;

    @Override
    public boolean isEdge() {
        return true;
    }

    public static GraphEdge of(String type) {
        GraphEdge graphEdge = new GraphEdge();
        graphEdge.setType(type);
        return graphEdge;
    }

    public GraphEdge name(String name) {
        super.setName(name);
        return this;
    }

    public GraphEdge addProperty(String key, Object value) {
        getProperties().put(key, value);
        return this;
    }

    public GraphEdge sourceId(String sourceId) {
        setSourceId(sourceId);
        return this;
    }

    public GraphEdge targetId(String targetId) {
        setTargetId(targetId);
        return this;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "GraphEdge{" +
                "sourceId='" + sourceId + '\'' +
                ", targetId='" + targetId + '\'' +
                '}';
    }
}
