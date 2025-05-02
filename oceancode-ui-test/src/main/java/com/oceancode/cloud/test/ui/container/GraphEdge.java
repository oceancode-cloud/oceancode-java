package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class GraphEdge extends GraphCell{
    private GraphNode source;
    private GraphNode target;
    public GraphEdge(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public GraphNode getSource() {
        return source;
    }

    public void setSource(GraphNode source) {
        this.source = source;
    }

    public GraphNode getTarget() {
        return target;
    }

    public void setTarget(GraphNode target) {
        this.target = target;
    }
}
