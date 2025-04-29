package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class CollapseItem extends UIContainer {
    public CollapseItem(UIContainer parent, Locator locator) {
        super(parent, locator);
    }


    public CollapseItem setActive(boolean val) {

        if (val) {
            if (!isActive()) {
                locator().click();
                return this;
            }
        } else {
            if (isActive()) {
                locator().click();
                return this;
            }
        }
        return this;
    }

    public boolean isActive() {
        return locator().locator(".is-active").count() > 0;
    }


}
