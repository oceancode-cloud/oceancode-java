package com.oceancode.cloud.api.file;

import java.io.File;
import java.util.function.Consumer;

public interface FileService {
    <T extends ParseFileContext> void parse(T context, ParseCallback callback);

    void readTemplate(File templateFile, Consumer<TemplateInputStream> consumer);

    <T extends ExportFileContext> void write(T context, WriteCallback callback);
}
