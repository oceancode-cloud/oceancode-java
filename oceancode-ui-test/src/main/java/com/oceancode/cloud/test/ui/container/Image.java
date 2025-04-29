package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Image extends UIContainer {
    public Image(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public String src() {
        return locator().getAttribute("src");
    }
}
