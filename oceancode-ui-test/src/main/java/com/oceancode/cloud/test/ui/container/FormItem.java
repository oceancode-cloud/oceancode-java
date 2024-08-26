package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class FormItem extends UIContainer{
    public FormItem(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public FormItem fill(String value) {
        locator().locator("input,textarea").locator("visible=true").fill(value);
        return this;
    }
}
