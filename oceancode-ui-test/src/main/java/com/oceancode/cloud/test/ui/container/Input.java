package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Input extends UIContainer {


    public Input(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public Input fill(String text) {
        locator().fill(text);
        return this;
    }
}
