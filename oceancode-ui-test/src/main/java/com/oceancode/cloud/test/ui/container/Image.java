package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Image extends UIContainer {


    public Image(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public String src() {
        return locator().getAttribute("src");
    }
}
