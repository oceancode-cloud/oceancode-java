package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

public class Tabs extends UIContainer {
    public Tabs(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public Tab tab(String label) {
        Locator it = tabHeader().getByRole(AriaRole.TAB, new Locator.GetByRoleOptions().setName(label));
        it.click();
        return new Tab(this, it);
    }

    private Locator tabHeader() {
        return locator().locator(UiUtil.containClass("-tabs__header"));
    }
}
