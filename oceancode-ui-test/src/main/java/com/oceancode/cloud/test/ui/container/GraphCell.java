package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class GraphCell extends UIContainer {

    public GraphCell(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public boolean isNode() {
        return locator().getAttribute("class").contains("-node");
    }

    public boolean isEdge() {
        return locator().getAttribute("class").contains("-edge");
    }

    public String getLabel() {
        Locator it = locator().locator("text");
        if (it.count() != 1) {
            it = locator().locator(".label");
        }
        if (it.count() != 1) {
            it = locator();
        }
        return it.innerText();
    }

    public String getLabel(String selector) {
        return locator().locator(selector).innerText();
    }
}
