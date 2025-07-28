package com.oceancode.cloud.api.excel;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileService {
    void parse(String path, InputStream inputStream, ParseCallback callback);

    void parse(String path, ParseCallback callback);

    void write(String templatePath, OutputStream outputStream, WritCallback callback);

    void write(String templatePath, WritCallback callback);
}
