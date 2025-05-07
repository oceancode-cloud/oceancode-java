package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.function.Supplier;

public class Message extends UIContainer {


    public Message(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
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
