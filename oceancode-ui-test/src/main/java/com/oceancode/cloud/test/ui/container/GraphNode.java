package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.Collections;
import java.util.List;

public class GraphNode extends GraphCell{
    public GraphNode(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public List<GraphNodePort> getPorts(){
        if(isEdge()){
            return Collections.emptyList();
        }

        Locator it = locator().locator(".x6-port");
        List<GraphNodePort> ports = it.all().stream().filter(e -> e.getAttribute("class").contains("x6-port"))
                .map(e -> new GraphNodePort(this, e))
                .toList();
        return ports;
    }
}
