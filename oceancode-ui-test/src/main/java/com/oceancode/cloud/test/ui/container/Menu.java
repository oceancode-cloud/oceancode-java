package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Menu extends UIContainer {

    public Menu(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public MenuItem item(String label) {
        return new MenuItem(this, () -> locator().getByText(label));
    }
}
