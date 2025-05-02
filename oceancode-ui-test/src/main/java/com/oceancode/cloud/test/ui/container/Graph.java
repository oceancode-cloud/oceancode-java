package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.stream.Collectors;

public class Graph extends UIContainer {
    public Graph(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public GraphNode getNodeById(String id) {
        Locator it = locator().locator(".x6-cell", new Locator.LocatorOptions().setHas(locator().locator("*[data-cell-id=" + id + "]")));
        return new GraphNode(this, it);
    }

    public List<GraphNode> getNodes() {
        return locator().locator(".x6-cell").all().stream().map(e -> new GraphNode(this, e))
                .filter(e -> e.isNode())
                .collect(Collectors.toList());
    }

    public GraphEdge getEdgeById(String id) {
        GraphNode node = getNodeById(id);
        return node.isEdge() ? new GraphEdge(this, node.locator()) : null;
    }

    public List<GraphEdge> getEdges() {
        List<GraphEdge> list = locator().locator("g").all().stream().filter(e -> e.getAttribute("class").contains("x6-edge"))
                .map(e -> new GraphEdge(this, e)).toList();
        return list;

    }
}
