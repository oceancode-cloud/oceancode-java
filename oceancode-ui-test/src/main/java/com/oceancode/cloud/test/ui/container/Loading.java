package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Loading extends UIContainer {
    public Loading(UIContainer parent, Locator locator) {
        super(parent, locator);
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
