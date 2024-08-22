package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Dialog extends UIContainer{
    public Dialog(Page page, UIContainer parent, Locator locator) {
        super(page, parent, locator);
    }


}
