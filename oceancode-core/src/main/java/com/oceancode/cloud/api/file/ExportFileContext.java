package com.oceancode.cloud.api.file;

import java.io.OutputStream;
import java.util.Map;

public class ExportFileContext extends FileContext {
    private String templateFile;

    private OutputStream outputStream;

    private Map<String, Object> variables;

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
