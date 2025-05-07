package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Radio extends UIContainer {


    public Radio(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public boolean isChecked() {
        Locator it = locator().locator(UiUtil.containClass("-checked"));
        return it.count() == 1 && it.isVisible();
    }
}
