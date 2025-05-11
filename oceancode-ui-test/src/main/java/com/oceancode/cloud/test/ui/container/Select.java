package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Select extends UIContainer {


    public Select(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public Select fill(String value) {
        locator().click();
        UiUtil.get(() ->
                options().stream()
                        .filter(e -> e.getText().equals(value))
                        .findFirst().orElse(null)).click();
        return this;
    }

    public List<SelectOption> options() {
        Locator it = locator().getByRole(AriaRole.OPTION);
        if (it.count() == 0) {
            Popover popover = UiUtil.rootContainer().popover();
            if (popover.count() == 1 && popover.isVisible()) {
                it = popover.locator().getByRole(AriaRole.OPTION);
            }
        }
        if (it.count() == 0) {
            return Collections.emptyList();
        }
        return it.all().stream().filter(e -> e.isVisible()).map(e -> new SelectOption(this, () -> e)).toList();

    }

}
