package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Form extends UIContainer {
    public Form(UIContainer parent, Locator locator) {
        super(parent, locator);
    }


    public FormItem item(String label) {
        Locator locator = locator().locator(getClassName("form-item"));
        locator = locator.filter(new Locator.FilterOptions().setHasText(label));
        return new FormItem(this, locator);
    }

    public FormItem item(int index) {
        Locator loc = locator().locator(getClassName("form-item")).all().get(index);
        return new FormItem(this, loc);
    }

    public List<FormItem> items() {
        return locator().locator(getClassName("form-item")).all()
                .stream().map(e -> new FormItem(this, e))
                .collect(Collectors.toList());

    }

    public Form fill(String label, String value) {
        item(label).fill(value);
        return this;
    }

    public Form fillByField(String field, String value) {
        locator().locator("*[name=" + field + "]").fill(value);
        return this;
    }

    public Form fillUsername(String username) {
        return fillByField("username", username);
    }

    public Form fillPassword(String password) {
        return fillByField("password", password);
    }
}
