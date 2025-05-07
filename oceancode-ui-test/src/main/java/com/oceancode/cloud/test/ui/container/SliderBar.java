package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class SliderBar extends UIContainer{


    public SliderBar(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }
}
