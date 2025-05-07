package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class GraphNode extends GraphCell {


    public GraphNode(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public List<GraphNodePort> getPorts() {
        if (isEdge()) {
            return Collections.emptyList();
        }

        Locator it = locator().locator(".x6-port");
        List<GraphNodePort> ports = it.all().stream().filter(e -> e.getAttribute("class").contains("x6-port"))
                .map(e -> new GraphNodePort(this, () -> e))
                .toList();
        return ports;
    }
}
