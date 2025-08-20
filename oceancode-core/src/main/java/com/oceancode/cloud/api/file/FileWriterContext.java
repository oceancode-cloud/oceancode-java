package com.oceancode.cloud.api.file;

import java.util.List;

public class FileWriterContext extends FileContext {
    private String filePath;
    private List<Column> columns;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
}
