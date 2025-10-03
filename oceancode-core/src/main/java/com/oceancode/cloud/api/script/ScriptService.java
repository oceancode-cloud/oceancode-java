package com.oceancode.cloud.api.script;

import java.util.Map;

public interface ScriptService {
    void executeScript(String filePath, String workDir, Map<String, String> envMap, LineCallback callback);

    default void executeScript(String filePath, Map<String, String> envMap, LineCallback callback) {
        executeScript(filePath, null, envMap, callback);
    }

    default void executeScript(String filePath, LineCallback callback) {
        executeScript(filePath, null, callback);
    }
}
