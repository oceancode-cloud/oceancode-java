package com.oceancode.cloud.api.file;

import java.io.InputStream;

public class ParseFileContext extends FileContext {
    private String filePath;
    private InputStream inputStream;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
