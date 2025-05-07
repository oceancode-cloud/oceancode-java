package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class List extends UIContainer {


    public List(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public ListItem item() {
        return new ListItem(this, () -> locator().locator("*[class$=-item]"));
    }

    public ListItem item(String label) {
        return new ListItem(this, () -> locator().locator("*[class$=-item]").filter(new Locator.FilterOptions().setHasText(label)));
    }
}
