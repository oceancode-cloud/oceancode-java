package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class MenuItem extends UIContainer{

    public MenuItem(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }
}
