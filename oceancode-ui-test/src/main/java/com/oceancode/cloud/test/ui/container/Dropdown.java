package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Dropdown extends UIContainer {
    public Dropdown(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public Dropdown select(String name) {
        findByText(name).click();
        return this;
    }

    public Dropdown select(String selector, String name) {
        UIContainer container = container(selector, name);
        container.click();
        return this;
    }
}
