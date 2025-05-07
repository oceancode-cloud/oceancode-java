package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Loading extends UIContainer {


    public Loading(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    @Override
    public void load() {
        Locator it = locator().locator(UiUtil.containClass("-loading"));
        if (it.count() == 0) {
            return;
        }
        UiUtil.waitForLoading(() -> !it.isVisible() || it.count() == 0);
    }

    public boolean isDone() {
        load();
        return true;
    }
}
