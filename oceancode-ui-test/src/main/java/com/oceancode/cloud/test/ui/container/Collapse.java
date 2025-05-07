package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Collapse extends UIContainer {


    public Collapse(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public CollapseItem item(String label) {
        return new CollapseItem(this, () -> {
            Locator it = locator()
                    .locator(UiUtil.containClass("-collapse-item"), new Locator.LocatorOptions().setHasText(label));
            if (it.count() > 1) {
                it = it.all().stream().filter(e -> e.isVisible()).findFirst().orElse(null);
            }
            return it;
        });
    }

}
