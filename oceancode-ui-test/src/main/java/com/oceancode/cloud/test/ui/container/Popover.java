package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Popover extends UIContainer{

    public Popover(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }
}
