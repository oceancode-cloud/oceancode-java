package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Dropdown extends UIContainer {
    public Dropdown(Page page, UIContainer parent, Locator locator) {
        super(page, parent, locator);
    }

    public Dropdown select(String name) {
        findByText(name).click();
        return this;
    }
}
