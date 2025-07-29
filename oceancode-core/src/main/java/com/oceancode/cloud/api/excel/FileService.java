package com.oceancode.cloud.api.excel;

import java.io.File;
import java.util.function.Consumer;

public interface FileService {
    <T extends ParseFileContext> void parse(T context, ParseCallback callback);

    <T extends ExportFileContext> void write(T context, WriteCallback callback);

    void readTemplate(File templateFile, Consumer<TemplateInputStream> consumer);

}
