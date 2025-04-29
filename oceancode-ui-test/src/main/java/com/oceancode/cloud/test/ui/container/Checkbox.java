package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Checkbox extends UIContainer {
    public Checkbox(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public void checked() {
        if (!isChecked()) {
            return;
        }
        locator().click();
    }

    public void unChecked() {
        if (isChecked()) {
            return;
        }
        locator().click();
    }

    public boolean isChecked() {
        return locator().locator(".is-checked").count() > 0;
    }
}
