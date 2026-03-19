package com.oceancode.cloud.plugin.api.object.method;

import java.util.List;

public class MethodCodePrototypeResult {
    private List<String> codes;
    private Integer methodStartLine;
    private Integer bodyStartLine;
    private Integer bodyEndLine;

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

    public Integer getBodyStartLine() {
        return bodyStartLine;
    }

    public void setBodyStartLine(Integer bodyStartLine) {
        this.bodyStartLine = bodyStartLine;
    }

    public Integer getBodyEndLine() {
        return bodyEndLine;
    }

    public void setBodyEndLine(Integer bodyEndLine) {
        this.bodyEndLine = bodyEndLine;
    }
}
