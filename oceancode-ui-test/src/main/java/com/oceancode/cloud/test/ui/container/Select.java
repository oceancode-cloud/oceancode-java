package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Select extends UIContainer {
    public Select(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public Select fill(String value){
        locator().click();
        locator().getByText(value).click();
        return this;
    }

}
