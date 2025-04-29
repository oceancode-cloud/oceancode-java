package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class RadioGroup extends UIContainer {
    public RadioGroup(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public void setValue(String value) {
        Locator it = locator().getByText(value);
        if (it.count() == 1) {
            it.click();
        }
    }
}
