package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Select extends UIContainer {


    public Select(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public Select fill(String value){
        locator().click();
        locator().getByText(value).click();
        return this;
    }

}
