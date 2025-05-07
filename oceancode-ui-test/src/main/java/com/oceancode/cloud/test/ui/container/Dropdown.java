package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.Objects;
import java.util.function.Supplier;

public class Dropdown extends UIContainer {


    public Dropdown(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public Dropdown select(String name) {
        findByText(name).click();
        return this;
    }

    public Dropdown select(String selector, String name) {
        UIContainer container = container(selector, name);
        container.click();
        return this;
    }

    @Override
    public Popover popover() {
        if (super.popover().exists()) {
            return super.popover();
        }
        if (Objects.nonNull(parent())) {
            if (parent().popover().exists()) {
                return parent().popover();
            }
        }
        return UiUtil.rootContainer().popover();
    }
}
