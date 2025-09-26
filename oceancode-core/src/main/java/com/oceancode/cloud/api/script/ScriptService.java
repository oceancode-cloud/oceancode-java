package com.oceancode.cloud.api.script;

import java.io.OutputStream;
import java.util.function.Function;

public interface ScriptService {
    void executeScript(String filePath, LineCallback callback);
}
