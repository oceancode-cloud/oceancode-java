package com.oceancode.cloud.test.ui.view;

import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

public class BaseView {
    private UIContainer container;

    public BaseView(UIContainer container) {
        this.container = container;
    }

    public BaseView() {
        this.container = UiUtil.rootContainer();
    }



    public UIContainer container() {
        return this.container;
    }

}
