package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

public class Message extends UIContainer {
    public Message(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public boolean isType(String type) {
        return container("div[class=*-" + type + " ").count() > 0;
    }

    public boolean isError() {
        return isType("error");
    }

    public boolean isInfo() {
        return isType("info");
    }

    public boolean isWarn() {
        return isType("warn");
    }

    public boolean isSuccess() {
        return isType("success");
    }
}
