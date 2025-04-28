package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Menu extends UIContainer {
    public Menu(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public MenuItem item(String label) {
        return new MenuItem(this, locator().getByText(label));
    }
}
