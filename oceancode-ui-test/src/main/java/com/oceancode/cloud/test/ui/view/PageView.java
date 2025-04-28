package com.oceancode.cloud.test.ui.view;

import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

public class PageView extends BaseView {

    private String url;

    public PageView(UIContainer container) {
        super(container);
    }

    public PageView(String url) {
        super(new UIContainer(null, UiUtil.getPage().locator("html")));
        this.url = url;
    }

    public PageView() {
        load();
    }

    public void load() {
        if (ValueUtil.isNotEmpty(url)) {
            UiUtil.getPage().navigate(url);
        }
    }
}
