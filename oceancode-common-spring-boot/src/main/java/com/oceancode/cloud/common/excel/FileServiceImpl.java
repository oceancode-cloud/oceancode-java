package com.oceancode.cloud.common.excel;

import com.oceancode.cloud.api.excel.DataRow;
import com.oceancode.cloud.api.excel.FileContext;
import com.oceancode.cloud.api.excel.FileService;
import com.oceancode.cloud.api.excel.ParseCallback;
import com.oceancode.cloud.api.excel.WritCallback;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FileServiceImpl implements FileService {
    @Override
    public void parse(String path, InputStream inputStream, ParseCallback callback) {
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
            FileContext fileContext = new FileContext();
            while (iterator.hasNext()) {
                Sheet sheet = iterator.next();
                fileContext.setName(sheet.getSheetName());
                if (callback.match(fileContext)) {
                    processSheet(fileContext, index++, sheet, callback, workbook);
                }
            }
        } catch (Throwable throwable) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, throwable);
        }
    }

    private void processSheet(FileContext fileContext, int partIndex, Sheet sheet, ParseCallback callback, Workbook workbook) {
        Iterator<Row> iterator = sheet.iterator();
        int rowIndex = 1;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            ExcelRow excelRow = new ExcelRow(partIndex, rowIndex, row, workbook);
            if (!callback.parse(fileContext, excelRow)) {
                break;
            }
        }
    }

    @Override
    public void parse(String path, ParseCallback callback) {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            parse(path, fileInputStream, callback);
        } catch (Throwable e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    @Override
    public void write(String templatePath, OutputStream outputStream, WritCallback callback) {
        if (ValueUtil.isEmpty(templatePath)) {
            Workbook workbook = new XSSFWorkbook();
            processWrite(workbook, callback);
            try {
                workbook.write(outputStream);
            } catch (IOException e) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
            }
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(templatePath)) {
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            processWrite(workbook, callback);
        } catch (Throwable e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    private static class InnerFileContext extends FileContext {
        private final List<String> columnKeys = new ArrayList<>();

        public List<String> getColumnKeys() {
            return columnKeys;
        }
    }

    private static class SheetObj {
        private final Sheet sheet;
        private int rowIndex;
        private final InnerFileContext fileContext;

        public SheetObj(InnerFileContext fileContext, Sheet sheet) {
            this.sheet = sheet;
            this.rowIndex = 0;
            this.fileContext = fileContext;
        }

        public void add(List<Map<String, Object>> values) {
            if (ValueUtil.isEmpty(values)) {
                return;
            }
            for (Map<String, Object> item : values) {
                Row row = sheet.createRow(rowIndex++);
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
                    if (value instanceof Double v) {
                        cell.setCellValue(v);
                    } else if (value instanceof Boolean v) {
                        cell.setCellValue(v);
                    } else if (value instanceof LocalDateTime v) {
                        cell.setCellValue(v);
                    } else {
                        cell.setCellValue(String.valueOf(value));
                    }
                }
            }
        }
    }

    private void processWrite(Workbook workbook, WritCallback callback) {
        Map<Integer, SheetObj> sheetObjMap = new ConcurrentHashMap<>();

        long total = 0L;

        InnerFileContext innerFileContext = new InnerFileContext();
        while (true) {
            if (total == Long.MAX_VALUE) {
                break;
            }
            List<DataRow> rows = callback.getRows();
            if (ValueUtil.isEmpty(rows)) {
                break;
            }
            for (DataRow row : rows) {
                if (ValueUtil.isEmpty(row.getValues())) {
                    continue;
                }
                SheetObj sheetObj = sheetObjMap.computeIfAbsent(row.getPartIndex(), id -> new SheetObj(innerFileContext, workbook.createSheet(row.getName())));
                sheetObj.add(row.getValues());
            }
            total++;
        }
    }

    @Override
    public void write(String templatePath, WritCallback callback) {

    }
}
