package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Table extends UIContainer {
    public Table(UIContainer parent, Locator locator) {
        super(parent, locator);
    }

    public List<TableCell> columns() {
        Locator it = locator().locator("table");
        if (it.count() == 0) {
            it = locator();
        }
        List<Locator> list = it.locator("thead").locator("th")
                .all();
        List<TableCell> cells = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            cells.add(new TableCell(i, this, list.get(i)));
        }
        return cells;
    }

    public TableColumnDataContainer column(String name) {
        TableCell tableCell = columns().stream().filter(e -> e.locator().getByText(name).count() > 0).findFirst().orElse(null);
        if (Objects.isNull(tableCell)) {
            return new TableColumnDataContainer(this, Collections.emptyList());
        }
        List<List<TableCell>> lists = tableData();
        List<TableCell> dataList = new ArrayList<>();
        for (List<TableCell> list : lists) {
            if (tableCell.getIndex() < list.size()) {
                dataList.add(list.get(tableCell.getIndex()));
            }
        }
        return new TableColumnDataContainer(this, dataList);
    }

    public List<List<TableCell>> tableData() {
        Locator it = locator().locator("table");
        if (it.count() == 0) {
            it = locator();
        }

        List<Locator> trs = it.locator("tbody").locator("tr").all();
        List<List<TableCell>> dataList = new ArrayList<>();
        for (Locator tr : trs) {
            List<TableCell> rowList = new ArrayList<>();
            List<Locator> tds = tr.locator("td").all();
            for (int i = 0; i < tds.size(); i++) {
                rowList.add(new TableCell(i, this, tds.get(i)));
            }

            dataList.add(rowList);
        }
        return dataList;
    }

    public List<TableCell> getRow(int index) {
        List<List<TableCell>> lists = tableData();
        if (index < lists.size()) {
            return lists.get(index);
        }
        return Collections.emptyList();
    }

    public TableCell getCell(int rowIndex, int colIndex) {
        List<TableCell> row = getRow(rowIndex);
        if (row.isEmpty()) {
            return null;
        }
        if (colIndex >= 0 && colIndex < row.size()) {
            return row.get(rowIndex);
        }
        return null;
    }

    public TableCell getCell(String column, int rowIndex) {
        List<TableCell> row = getRow(rowIndex);
        if (row.isEmpty()) {
            return null;
        }
        List<TableCell> columns = columns();
        TableCell tableCell = columns.stream().filter(e -> e.locator().getByText(column).count() > 0).findAny().orElse(null);
        if (Objects.isNull(tableCell)) {
            return null;
        }

        if (tableCell.getIndex() < row.size()) {
            return row.get(tableCell.getIndex());
        }
        return null;
    }

    public TableCell getColumnCell(String columnName, String value) {
        TableCell tableCell = columns().stream().filter(e -> e.locator().getByText(columnName).count() == 1).findFirst().orElse(null);
        if (Objects.isNull(tableCell)) {
            return null;
        }

        List<List<TableCell>> lists = tableData();
        if (tableCell.getIndex() >= lists.size()) {
            return null;
        }
        for (List<TableCell> list : lists) {
            Locator it = list.get(tableCell.getIndex()).locator().getByText(value);
            if (it.count() == 1) {
                return list.get(tableCell.getIndex());
            }
        }
        return null;
    }

    public TableCell getRandomColumnCell(String columnName) {
        TableCell tableCell = columns().stream().filter(it -> it.locator().getByText(columnName).count() == 1).findFirst().orElse(null);
        return getRandomColumnCell(tableCell);
    }

    private TableCell getRandomColumnCell(TableCell tableCell) {
        if (Objects.isNull(tableCell)) {
            return null;
        }

        List<List<TableCell>> lists = tableData();
        int index = new Random().nextInt(0, lists.size()) % lists.size();
        List<TableCell> rowData = lists.get(index);
        if (tableCell.getIndex() < rowData.size()) {
            return rowData.get(tableCell.getIndex());
        }
        return null;
    }

    public TableCell getRandomCell() {
        List<TableCell> columns = columns();
        int index = new Random().nextInt(0, columns.size()) % columns.size();
        return getRandomColumnCell(columns.get(index));
    }

    public boolean isEmpty() {
        return tableData().isEmpty();
    }
}
