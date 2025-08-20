package com.oceancode.cloud.api.file;

public interface FileWriter {
    <T extends FileWriterContext> void write(T context, WriteCallback callback);
}
