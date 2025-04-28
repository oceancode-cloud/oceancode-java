package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.List;
import java.util.Objects;
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

    public FormItem item() {
        return items().stream().filter(it -> it.locator("input[type=text]").count() == 1).findFirst()
                .orElse(null);
    }

    public FormItem password() {
        return items().stream().filter(it -> it.locator("input[type=password]").count() == 1).findFirst()
                .orElse(null);
    }


    public FormItem username() {
        return item();
    }

    public List<FormItem> items() {
        return locator().locator("*[class*=-form-item]").all()
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

    public void submit() {
        if (parent() instanceof Dialog) {
            ((Dialog) parent()).submit();
            return;
        }

        if (Objects.nonNull(parent())) {
            if (parent().container("button").count() == 1) {
                parent().container("button").click();
                return;
            }
        } else if (page().locator("button").count() == 1) {
            page().locator("button").click();
            return;
        }

        submit(null);
    }

    public void submit(String selector) {
        if (ValueUtil.isEmpty(selector)) {
            container(selector).click();
            return;
        }
        if (button().count() == 1) {
            button().click();
            return;
        }
        if (button().count() == 0) {
            if (Objects.nonNull(parent())) {
                if (parent().button().count() == 1) {
                    parent().button().click();
                }
            }
        }
    }
}
