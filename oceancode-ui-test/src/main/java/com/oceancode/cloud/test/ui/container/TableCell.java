package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.oceancode.cloud.common.util.ValueUtil;

public class TableCell extends UIContainer {
    private int index;

    public TableCell(int index, UIContainer parent, Locator locator) {
        super(parent, locator);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String value(String selector) {
        Locator locator = locator().locator(selector);
        if (ValueUtil.isEmpty(selector)) {
            locator = locator().locator(UiUtil.containClass("-text"));
            if (locator.count() == 0) {
                locator = locator().locator(UiUtil.containClass("-title"));
            }
        }
        if (locator.count() == 1) {
            return locator.innerText();
        }

        return null;
    }

    public String value() {
        return value(null);
    }
}
