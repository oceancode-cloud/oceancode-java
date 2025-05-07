package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Card extends UIContainer{

    public Card(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }
}
