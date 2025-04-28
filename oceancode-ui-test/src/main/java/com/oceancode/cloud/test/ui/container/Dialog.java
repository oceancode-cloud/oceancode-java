package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Dialog extends UIContainer {
    public Dialog(UIContainer parent, Locator locator) {
        super(parent, locator);
    }


    public void submit() {
        Locator it = locator().locator("*[class$=__footer]");
        if (it.count() == 1) {
            Locator button = it.locator("button");
            if (button.count() == 1) {
                button.click();
            }
        }
    }

}
