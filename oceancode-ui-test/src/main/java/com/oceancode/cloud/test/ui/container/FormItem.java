package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class FormItem extends UIContainer {

    public FormItem(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public FormItem fill(String value) {
        locator().locator("input,textarea").locator("visible=true").fill(value);
        return this;
    }

    public boolean isError() {
        return error().count() > 0;
    }

    public String errorMessage() {
        if (isError()) {
            return error().innerText();
        }
        return "";
    }

    private Locator error() {
        return locator().locator(UiUtil.containClass("__error"));
    }

    public boolean isInput() {
        return input().exists();
    }

    public boolean isRadioGroup() {
        return radioGroup().exists();
    }

    public boolean isSelect() {
        return select().exists();
    }

}
