package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Collapse extends UIContainer {
    public Collapse(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public CollapseItem item(String label) {
        Locator it = locator()
                .locator(UiUtil.containClass("-collapse-item"), new Locator.LocatorOptions().setHasText(label));
        if (it.count() > 1) {
            it = it.all().stream().filter(e -> e.isVisible()).findFirst().orElse(null);
        }

        return new CollapseItem(this, it);
    }

}
