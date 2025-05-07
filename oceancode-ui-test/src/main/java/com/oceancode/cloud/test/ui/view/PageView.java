package com.oceancode.cloud.test.ui.view;

import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.ui.container.Loading;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

import java.util.Objects;

public class PageView extends BaseView {

    private String url;

    public PageView(UIContainer container) {
        super(container);
    }

    public PageView(String url) {
        super(new UIContainer(null, () -> UiUtil.getPage().locator("html")));
        this.url = url;
    }

    public PageView() {
        load();
    }

    public void load(UIContainer container) {
        if (ValueUtil.isNotEmpty(url)) {
            UiUtil.getPage().navigate(url);

            UiUtil.waitForLoading(() -> {
                String currentUrl = UiUtil.getPage().evaluate("window.location.href") + "";
                if (currentUrl.contains("?")) {
                    currentUrl = currentUrl.substring(0, currentUrl.indexOf("?"));
                }
                boolean ret = this.url.startsWith(currentUrl);

                Object state = UiUtil.getPage().evaluate("document.readyState");
                ret = ret && "complete".equals(state);
                if (Objects.nonNull(container)) {
                    ret = ret && container.count() > 0 && container.locator().isVisible();
                }
                return ret;
            });
        }
    }

    public Loading loading() {
        return new Loading(null, () -> container().locator());
    }

    public void load() {
        load(null);
    }

    public String getUrl() {
        return UiUtil.getPage().evaluate("window.location.href") + "";
    }
}
