package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.oceancode.cloud.common.util.ValueUtil;

public class UIContainer {
    private Page page;
    private UIContainer parent;
    private Locator locator;

    public UIContainer(Page page, UIContainer parent, Locator locator) {
        this.page = page;
        this.parent = parent;
        this.locator = locator;
    }

    public Page page() {
        return this.page;
    }

    public UIContainer parent() {
        return this.parent;
    }

    public Locator locator() {
        return this.locator;
    }

    public UIContainer container(String selector, String label) {
        Locator loc = this.locator.locator(selector);
        if (ValueUtil.isNotEmpty(label)) {
            loc = loc.filter(new Locator.FilterOptions().setHasText(label));
        }

        return new UIContainer(page, this, loc);
    }

    public UIContainer container(String selector) {
        return container(selector, null);
    }

    public Form form() {
        return new Form(page, this, locator().locator("form"));
    }

    protected String getClassName(String className) {
        return "." + UiUtil.getFrameworkTag() + "-" + className;
    }

    public UIContainer button(String label) {
        return new UIContainer(page, this, locator.locator("button").filter(new Locator.FilterOptions().setHasText(label)));
    }

    public void click() {
        locator.click();
    }

    public UIContainer findByText(String text) {
        return new UIContainer(page, this, locator.filter(new Locator.FilterOptions().setHasText(text)));
    }

    public UIContainer findByTitle(String text) {
        return findByAttr("title", text);
    }

    public UIContainer findByAttr(String attr, String value) {
        return new UIContainer(page, this, locator.locator("*[" + attr + "=" + value + "]"));
    }

    public Dialog dialog(String title) {
        Locator loc = page.locator("div[role=dialog]");
        if (ValueUtil.isNotEmpty(title)) {
            loc = loc.filter(new Locator.FilterOptions().setHasText(title));
        }
        return new Dialog(page, this, loc);
    }

    public Dialog dialog() {
        return dialog(null);
    }
}
