package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class FormItem extends UIContainer{
    public FormItem(Page page, UIContainer parent, Locator locator) {
        super(page, parent, locator);
    }

    public FormItem fill(String value) {
        locator().locator("input,textarea").locator("visible=true").fill(value);
        return this;
    }
}
