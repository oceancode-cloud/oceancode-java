package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

import java.util.function.Supplier;

public class Tabs extends UIContainer {

    public Tabs(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public Tab tab(String label) {
        return new Tab(this, () -> {
            Locator it = tabHeader().getByRole(AriaRole.TAB, new Locator.GetByRoleOptions().setName(label));
            it.click();
            return it;
        });
    }

    private Locator tabHeader() {
        return locator().locator(UiUtil.containClass("-tabs__header"));
    }
}
