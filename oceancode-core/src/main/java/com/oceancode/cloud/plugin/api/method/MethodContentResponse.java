package com.oceancode.cloud.plugin.api.method;

import java.util.List;

public class MethodContentResponse {
    private List<String> codes;
    private Integer methodStartLine;
    private Integer bodyEndLine;
    private Integer bodyStartLine;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public Integer getMethodStartLine() {
        return methodStartLine;
    }

    public void setMethodStartLine(Integer methodStartLine) {
        this.methodStartLine = methodStartLine;
    }

    public Integer getBodyEndLine() {
        return bodyEndLine;
    }

    public void setBodyEndLine(Integer bodyEndLine) {
        this.bodyEndLine = bodyEndLine;
    }

    public Integer getBodyStartLine() {
        return bodyStartLine;
    }

    public void setBodyStartLine(Integer bodyStartLine) {
        this.bodyStartLine = bodyStartLine;
    }
}
