package com.oceancode.cloud.test.ui.view;

import com.microsoft.playwright.Page;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

import java.util.Objects;
import java.util.function.Supplier;

public class PageView extends BaseView {

    private String url;
    protected Page page;

    public PageView(UIContainer container) {
        super(container);
        page = UiUtil.getPage();
        load(container());
    }

    public PageView(String url) {
        super(new UIContainer(null, () -> UiUtil.getPage().locator("html")));
        this.url = url;
        page = UiUtil.getPage();
        load(container());
    }

    private void load(UIContainer container) {
        if (ValueUtil.isNotEmpty(url)) {
            UiUtil.getPage().navigate(url);

            UiUtil.waitForLoading(() -> {
                String currentUrl = UiUtil.getPage().evaluate("window.location.origin") + "";
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

    public static <T extends UIContainer> T get(Supplier<T> supplier) {
        supplier.get();
        return UiUtil.get(supplier);
    }

    public static <T extends UIContainer> T get(Runnable runnable, Supplier<T> supplier) {
        runnable.run();
        supplier.get();
        UiUtil.waitForLoading(() -> {
            try {
                T t = supplier.get();
                boolean ret = t.count() > 0 || (t.count() == 1 && t.isVisible());
                if (!ret) {
                    runnable.run();
                }
                return ret;
            } catch (Throwable throwable) {
                runnable.run();
                supplier.get();
                System.err.println(throwable);
            }
            return false;
        });
        return supplier.get();
    }

    public String getUrl() {
        return UiUtil.getPage().evaluate("window.location.href") + "";
    }
}
