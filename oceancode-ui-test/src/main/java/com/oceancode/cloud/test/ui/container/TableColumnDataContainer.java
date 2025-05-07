package com.oceancode.cloud.test.ui.container;


import com.microsoft.playwright.Locator;

import java.util.Objects;
import java.util.function.Function;

public class TableColumnDataContainer extends UIContainer {
    private java.util.List<TableCell> tableCellList;

    public TableColumnDataContainer(UIContainer parent, java.util.List<TableCell> tableCellList) {
        super(parent, null);
        this.tableCellList = tableCellList;
    }

    @Override
    public Locator locator(String selector) {
        TableCell tableCell = findCell(it -> it.locator(selector).count() > 0);
        if (Objects.nonNull(tableCell)) {
            return tableCell.locator(selector);
        }
        return null;
    }

    private TableCell findCell(Function<Locator, Boolean> function) {
        return tableCellList.stream().filter(e -> function.apply(e.locator()))
                .findFirst().orElse(null);
    }

    @Override
    public UIContainer findByText(String text) {
        TableCell cell = findCell(it -> it.getByText(text).count() > 0);
        return Objects.nonNull(cell) ? new UIContainer(this, () -> cell.locator().getByText(text)) : null;
    }

}
