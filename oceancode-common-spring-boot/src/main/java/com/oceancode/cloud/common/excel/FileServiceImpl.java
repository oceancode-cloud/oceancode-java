package com.oceancode.cloud.common.excel;

import com.oceancode.cloud.api.excel.DataRow;
import com.oceancode.cloud.api.excel.ExportFileContext;
import com.oceancode.cloud.api.excel.FileContext;
import com.oceancode.cloud.api.excel.FileService;
import com.oceancode.cloud.api.excel.ParseCallback;
import com.oceancode.cloud.api.excel.ParseFileContext;
import com.oceancode.cloud.api.excel.TemplateInputStream;
import com.oceancode.cloud.api.excel.WriteCallback;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ExpressUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FileServiceImpl implements FileService {
    @Override
    public <T extends ParseFileContext> void parse(T context, ParseCallback callback) {
        parse(context, callback, true);
    }

    private void parse(ParseFileContext fileContext, ParseCallback callback, boolean autoClose) {
        String path = fileContext.getFilePath();
        InputStream inputStream = fileContext.getInputStream();
        if (ValueUtil.isEmpty(path) || Objects.isNull(inputStream)) {
            return;
        }

        Workbook workbook = null;
        try {
            ZipSecureFile.setMinInflateRatio(0);
            String temp = path.toLowerCase().trim();
            if (temp.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (temp.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            }
            if (Objects.isNull(workbook)) {
                return;
            }

            Iterator<Sheet> iterator = workbook.iterator();
            int index = 1;
            if (fileContext instanceof InnerFileContext innerFileContext) {
                innerFileContext.setWorkbook(workbook);
            }
            while (iterator.hasNext()) {
                Sheet sheet = iterator.next();
                fileContext.setName(sheet.getSheetName());
                if (fileContext instanceof InnerFileContext innerFileContext) {
                    innerFileContext.getNames().add(sheet.getSheetName());
                }
                if (callback.match(fileContext)) {
                    processSheet(fileContext, index, sheet, callback, workbook);
                }
                index++;
            }
        } catch (Throwable throwable) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, throwable);
        } finally {
            if (autoClose) {
                if (Objects.nonNull(workbook)) {
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
                    }
                }
            }
        }
    }

    private void processSheet(FileContext fileContext, int partIndex, Sheet sheet, ParseCallback callback, Workbook workbook) {
        for (Row row : sheet) {
            ExcelRow excelRow = new ExcelRow(partIndex, row.getRowNum(), row, workbook);
            if (!callback.parse(fileContext, excelRow)) {
                break;
            }
        }
    }


    @Override
    public <T extends ExportFileContext> void write(T context, WriteCallback callback) {
        InnerFileContext innerFileContext = new InnerFileContext();
        String templatePath = context.getTemplateFile();
        innerFileContext.setVariables(context.getVariables());
        if (ValueUtil.isEmpty(templatePath)) {
            Workbook workbook = null;
            String temp = templatePath.toLowerCase().trim();
            if (temp.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook();
            } else if (temp.endsWith(".xls")) {
                workbook = new HSSFWorkbook();
            }
            if (Objects.isNull(workbook)) {
                return;
            }
            innerFileContext.setWorkbook(workbook);
            processWrite(innerFileContext, workbook, callback);
            try {
                workbook.write(context.getOutputStream());
            } catch (IOException e) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
                }
            }
            return;
        }

        File templateFile = new File(context.getTemplateFile());
        readTemplate(templateFile, e -> parseTemplate(innerFileContext, context, e.getInputStream(), () -> {
            Workbook workbook = innerFileContext.workbook;
            processWrite(innerFileContext, workbook, callback);

            try {
                workbook.write(context.getOutputStream());
            } catch (IOException ex) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, ex);
            } finally {
                try {
                    workbook.close();
                } catch (IOException ex) {
                    throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, ex);
                }
            }
        }));
    }

    private <T extends ExportFileContext> void parseTemplate(InnerFileContext innerFileContext, T context, InputStream inputStream, Runnable runnable) {
        List<ExcelRow> rows = new ArrayList<>();
        InnerFileContext fileContext = new InnerFileContext();
        fileContext.setInputStream(inputStream);
        fileContext.setFilePath(context.getTemplateFile());

        parse(fileContext, new ParseCallback() {
            @Override
            public boolean parse(FileContext context, com.oceancode.cloud.api.excel.Row row) {
                rows.add((ExcelRow) row);
                return true;
            }

            @Override
            public boolean match(FileContext context) {
                return true;
            }
        }, false);

        innerFileContext.setNames(fileContext.getNames());
        innerFileContext.setWorkbook(fileContext.workbook);
        for (ExcelRow row : rows) {
            Map<Integer, ExcelRow> rowMap = innerFileContext.getTemplateSheetRowMapping().get(row.getPartIndex());
            if (Objects.isNull(rowMap)) {
                rowMap = new HashMap<>();
                innerFileContext.getTemplateSheetRowMapping().put(row.getPartIndex(), rowMap);
            }
            rowMap.put(row.getIndex(), row);
        }
        runnable.run();
    }

    private static class InnerFileContext extends ParseFileContext {
        private final List<String> columnKeys = new ArrayList<>();
        private Workbook workbook;
        private Map<String, Object> variables;
        private List<String> names = new ArrayList<>();
        private Map<Integer, Map<Integer, ExcelRow>> templateSheetRowMapping = new ConcurrentHashMap<>();
        private Map<Integer, ExcelRow> templateSheetMapping = new ConcurrentHashMap<>();

        public List<String> getColumnKeys() {
            return columnKeys;
        }

        public void setWorkbook(Workbook workbook) {
            this.workbook = workbook;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

        public Map<Integer, Map<Integer, ExcelRow>> getTemplateSheetRowMapping() {
            return templateSheetRowMapping;
        }

        public void setTemplateSheetRowMapping(Map<Integer, Map<Integer, ExcelRow>> templateSheetRowMapping) {
            this.templateSheetRowMapping = templateSheetRowMapping;
        }

        public Map<Integer, ExcelRow> getTemplateSheetMapping() {
            return templateSheetMapping;
        }

        public void setTemplateSheetMapping(Map<Integer, ExcelRow> templateSheetMapping) {
            this.templateSheetMapping = templateSheetMapping;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }

        public ExcelRow getSheetTemplate(int sheetIndex) {
            return templateSheetMapping.computeIfAbsent(sheetIndex, id ->
                    getTemplateSheetRowMapping().computeIfAbsent(sheetIndex, it -> Collections.emptyMap()).values()
                            .stream().filter(it ->
                                    it.getValues().stream().anyMatch(e -> {
                                        if (e instanceof String str) {
                                            boolean ret = ValueUtil.isNotEmpty(str) && str.contains("$");
                                            return ret && str.replace(" ", "").contains("list.");
                                        }
                                        return false;
                                    })).findFirst().orElse(null));
        }
    }

    private static class SheetObj {
        private final Sheet sheet;
        private int rowIndex;
        private final int partIndex;
        private final InnerFileContext fileContext;
        private ExcelRow templateRow;

        public SheetObj(int partIndex, int rowIndex, InnerFileContext fileContext, Sheet sheet) {
            this.sheet = sheet;
            this.rowIndex = rowIndex;
            this.fileContext = fileContext;
            this.partIndex = partIndex;
            templateRow = fileContext.getSheetTemplate(partIndex);
            if (Objects.nonNull(templateRow)) {
                this.rowIndex = rowIndex + templateRow.getIndex();
            }
        }

        public void add(List<Map<String, Object>> values) {
            if (ValueUtil.isEmpty(values)) {
                return;
            }
            boolean hasTemplate = Objects.nonNull(templateRow);

            for (Map<String, Object> item : values) {
                Row row = getRow();
                if (hasTemplate) {
                    for (int cellIndex = 0; cellIndex < templateRow.getValues().size(); cellIndex++) {
                        Cell cell = getRowCell(row, cellIndex);
                        if (Objects.isNull(templateRow.getValue(cellIndex))) {
                            cell.setBlank();
                            continue;
                        }
                        String template = (String) templateRow.getValue(cellIndex);
                        if (ValueUtil.isEmpty(template) || !template.contains("$")) {
                            cell.setCellValue(template);
                            continue;
                        }

                        Map<String, Object> params = new HashMap<>(Objects.nonNull(fileContext.getVariables()) ? fileContext.getVariables().size() + 1 : 1);
                        if (Objects.nonNull(fileContext.getVariables())) {
                            params.putAll(fileContext.getVariables());
                        }
                        params.put("list", item);
                        Object value = ExpressUtil.parse(template, params, Object.class, true);
                        setCellValue(cell, value);
                    }
                    continue;
                }
                if (fileContext.getColumnKeys().isEmpty()) {
                    fileContext.getColumnKeys().addAll(item.keySet());
                }
                for (int i = 0; i < fileContext.getColumnKeys().size(); i++) {
                    Object value = item.get(fileContext.getColumnKeys().get(i));
                    Cell cell = row.createCell(i);
                    if (Objects.isNull(value)) {
                        cell.setBlank();
                        continue;
                    }
                    setCellValue(cell, value);
                }
            }
        }


        public void setRowValue(ExcelRow row) {
            Row r = sheet.getRow(row.getIndex());
            for (int i = 0; i < row.getValues().size(); i++) {
                Object value = row.getValues().get(i);
                if (value instanceof String str) {
                    value = ExpressUtil.parse(str, fileContext.getVariables(), Object.class, true);
                }
                setCellValue(r.getCell(i), value);
            }
        }

        private void setCellValue(Cell cell, Object value) {
            if (Objects.isNull(value)) {
                cell.setBlank();
                return;
            }
            if (value instanceof Double v) {
                cell.setCellValue(v);
            } else if (value instanceof Boolean v) {
                cell.setCellValue(v);
            } else if (value instanceof LocalDateTime v) {
                cell.setCellValue(v);
            } else if (value instanceof Integer v) {
                cell.setCellValue(v);
            } else if (value instanceof Date v) {
                cell.setCellValue(v);
            } else if (value instanceof LocalDate v) {
                cell.setCellValue(v);
            } else if (value instanceof Calendar v) {
                cell.setCellValue(v);
            } else if (value instanceof RichTextString v) {
                cell.setCellValue(v);
            } else {
                cell.setCellValue(String.valueOf(value));
            }
        }

        private Cell getRowCell(Row row, int cellIndex) {
            Cell cell = row.getCell(cellIndex);

            Cell oldCell = null;
            if (Objects.isNull(cell)) {
                cell = row.createCell(cellIndex);
                if (Objects.nonNull(this.templateRow) && row != this.templateRow) {
                    oldCell = this.templateRow.getRow().getCell(cellIndex);
                }
            }
            if (Objects.nonNull(oldCell)) {
                CellType cellType = oldCell.getCellType();
                if (CellType.FORMULA.equals(cellType)) {
                    cell.setCellFormula(oldCell.getCellFormula());
                }
                cell.setCellStyle(oldCell.getCellStyle());
                cell.setHyperlink(oldCell.getHyperlink());
                cell.setCellComment(oldCell.getCellComment());
            }
            return cell;
        }

        private Row getRow() {
            Row row = sheet.getRow(rowIndex);
            Row result = Objects.nonNull(row) ? row : sheet.createRow(rowIndex);
            if (Objects.nonNull(templateRow) && result != templateRow.getRow()) {
                Row oldRow = templateRow.getRow();
                result.setRowStyle(oldRow.getRowStyle());
                result.setHeight(oldRow.getHeight());
                result.setZeroHeight(oldRow.getZeroHeight());
            }
            rowIndex++;
            return result;
        }
    }

    private void processWrite(InnerFileContext context, Workbook workbook, WriteCallback callback) {
        Map<Integer, SheetObj> sheetObjMap = new ConcurrentHashMap<>();

        long total = 0L;

        InnerFileContext innerFileContext = context;
        while (true) {
            if (total == Long.MAX_VALUE) {
                break;
            }
            List<DataRow> rows = callback.getRows();
            if (ValueUtil.isEmpty(rows)) {
                break;
            }
            for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                DataRow row = rows.get(rowIndex);
                if (ValueUtil.isEmpty(row.getValues())) {
                    continue;
                }
                if (Objects.nonNull(innerFileContext.getTemplateSheetRowMapping())) {
                    if (!innerFileContext.getTemplateSheetRowMapping().containsKey(row.getPartIndex())) {
                        continue;
                    }
                }

                int finalRowIndex = rowIndex;
                SheetObj sheetObj = sheetObjMap.computeIfAbsent(row.getPartIndex(), id -> {
                    String name = innerFileContext.getNames().get(row.getPartIndex());
                    if (ValueUtil.isNotEmpty(innerFileContext.getVariables())) {
                        name = ExpressUtil.parse(name, innerFileContext.getVariables(), String.class, true);
                    }
                    if (ValueUtil.isEmpty(name)) {
                        name = innerFileContext.getNames().get(row.getPartIndex());
                    }
                    Sheet sheet = workbook.getSheetAt(row.getPartIndex());
                    workbook.setSheetName(row.getPartIndex(), name);
                    return new SheetObj(row.getPartIndex(), finalRowIndex, innerFileContext, sheet);
                });
                sheetObj.add(row.getValues());
            }
            total++;
        }

        if (ValueUtil.isEmpty(innerFileContext.getTemplateSheetRowMapping())) {
            return;
        }
        for (Map.Entry<Integer, Map<Integer, ExcelRow>> entry : innerFileContext.getTemplateSheetRowMapping().entrySet()) {
            ExcelRow excelRow = innerFileContext.getTemplateSheetMapping().get(entry.getKey());
            SheetObj sheetObj = sheetObjMap.get(entry.getKey());
            for (ExcelRow value : entry.getValue().values()) {
                if (value == excelRow) {
                    continue;
                }
                sheetObj.setRowValue(value);
            }

        }
    }

    @Override
    public void readTemplate(File templateFile, Consumer<TemplateInputStream> consumer) {
        try (TemplateInputStream templateInputStream = new TemplateInputStream(templateFile)) {
            consumer.accept(templateInputStream);
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}
