package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Input extends UIContainer {
    public Input(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public Input fill(String text) {
        locator().fill(text);
        return this;
    }
}
