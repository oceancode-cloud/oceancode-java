package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

import java.util.function.Supplier;

public class Tab extends UIContainer {

    public Tab(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    private Locator tabContent() {
        return parent().locator().locator(UiUtil.containClass("-tabs__content"));
    }

    private Locator activeContent() {
        return tabContent().getByRole(AriaRole.TABPANEL).all()
                .stream().filter(e -> "false".equals(e.getAttribute("aria-hidden")))
                .findFirst().orElse(null);
    }

    @Override
    public Locator locator() {
        return activeContent();
    }

    @Override
    public UIContainer container(String selector) {
        return new UIContainer(this, () -> activeContent());
    }

    @Override
    public UIContainer container(String selector, String label) {
        return new UIContainer(this, () -> activeContent()).container(selector, label);
    }


    @Override
    public Locator locator(String selector) {
        return activeContent().locator(selector);
    }
}
