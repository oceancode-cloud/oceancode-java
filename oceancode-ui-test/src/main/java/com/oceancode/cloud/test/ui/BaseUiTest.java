package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;
import com.oceancode.cloud.test.ui.page.BasePage;


public class BaseUiTest<T extends BasePage> {


    private UIContainer container;

    public BaseUiTest() {
        container = new UIContainer(UiUtil.getPage(), null, UiUtil.getPage().locator("html"));
    }


    protected UIContainer container() {
        return this.container;
    }
}
