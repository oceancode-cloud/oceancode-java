package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.function.Supplier;

public class RadioGroup extends UIContainer {


    public RadioGroup(UIContainer parent, Supplier<Locator> getFunction) {
        super(parent, getFunction);
    }

    public void setValue(String value) {
        Locator it = locator().getByText(value);
        if (it.count() == 1) {
            it.click();
        }
    }

    public Radio getCheckedItem() {
        return items().stream().filter(e -> e.isChecked()).findFirst().orElse(null);
    }

    public List<Radio> items() {
        return locator().locator(UiUtil.containClass("-radio")).all()
                .stream().filter(e -> e.isVisible())
                .filter(e -> e.locator(UiUtil.containClass("-checked")).count() == 1)
                .map(e -> new Radio(this, ()->e))
                .toList();
    }
}
