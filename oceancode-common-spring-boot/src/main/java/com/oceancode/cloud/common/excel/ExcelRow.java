package com.oceancode.cloud.common.excel;

import com.oceancode.cloud.api.excel.Row;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ExcelRow implements Row {
    private org.apache.poi.ss.usermodel.Row row;
    private int index;
    private Workbook workbook;

    private List<Object> list;
    private FormulaEvaluator formulaEvaluator;
    private boolean parsed;
    private Integer partIndex;

    public ExcelRow(Integer partIndex, int index, org.apache.poi.ss.usermodel.Row row, Workbook workbook) {
        this.partIndex = partIndex;
        this.index = index;
        this.row = row;
        this.workbook = workbook;

    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getSize() {
        return this.row.getRowNum();
    }

    @Override
    public List<Object> getValues() {
        if (parsed) {
            return list;
        }
        list = new ArrayList<>(row.getRowNum());
        Iterator<Cell> iterator = row.iterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            CellType cellType = cell.getCellType();
            if (Objects.isNull(cellType)) {
                list.add(null);
                continue;
            }
            processCell(cell);
        }
        parsed = true;
        return list;
    }

    private void processCell(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC -> list.add(cell.getNumericCellValue());
            case STRING, BLANK -> list.add(cell.getStringCellValue());
            case FORMULA -> {
                if (Objects.isNull(formulaEvaluator)) {
                    if (workbook instanceof HSSFWorkbook wk) {
                        formulaEvaluator = new HSSFFormulaEvaluator(wk);
                    } else if (workbook instanceof XSSFWorkbook xh) {
                        formulaEvaluator = new XSSFFormulaEvaluator(xh);
                    }
                }
                if (Objects.nonNull(formulaEvaluator)) {
                    CellValue cellValue = formulaEvaluator.evaluate(cell);
                    CellType valueType = cellValue.getCellType();
                    switch (valueType) {
                        case NUMERIC -> list.add(cellValue.getNumberValue());
                        case BOOLEAN -> list.add(cellValue.getBooleanValue());
                        case _NONE -> list.add(null);
                        default -> list.add(cellValue.getStringValue());
                    }
                }
            }
            case _NONE -> list.add(null);
            default -> list.add(cell.getStringCellValue());
        }
    }

    @Override
    public Object getValue(int index) {
        return null;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getText() {
        return "";
    }
}
