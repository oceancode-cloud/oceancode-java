package com.oceancode.cloud.test.ui.page;

import com.microsoft.playwright.Page;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

public class BasePage {
    private UIContainer container;
    private Page page;

    public BasePage(UIContainer container) {
        this.container = container;
        page = UiUtil.getPage();
    }

    public void load() {

    }

    public UIContainer container() {
        return this.container;
    }

}
