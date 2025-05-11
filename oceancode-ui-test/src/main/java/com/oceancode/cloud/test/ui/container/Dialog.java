package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.function.Supplier;

public class Dialog extends UIContainer {
    public Dialog(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
        UiUtil.waitForLoading(() -> locator().count() != 0 || !locator().isVisible());
    }

    public void submit() {
        UiUtil.waitForLoading(() -> {
            Locator it = locator().locator("*[class$=__footer]");
            if (it.count() == 1) {
                Locator button = it.locator("button");
                if (button.count() == 1) {
                    UiUtil.waitForLoading(() -> {
                        if (button.isVisible()) {
                            button.click();
                        }
                        return button.count() == 0 || !button.isVisible();
                    });
                }
            }
            return false;
        });
    }

    private Locator getHeader() {
        return locator().locator("header").first();
    }

    public void close() {
        if (getHeader().count() == 0) {
            return;
        }

        Locator locator = locator().locator(UiUtil.containClass("__close"));
        if (locator.count() == 1) {
            locator.click();
        }
    }

    public String title() {
        return locator().getAttribute("aria-label");
    }
}
