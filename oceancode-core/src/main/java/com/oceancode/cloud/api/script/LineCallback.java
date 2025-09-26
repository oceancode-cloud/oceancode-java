package com.oceancode.cloud.api.script;

@FunctionalInterface
public interface LineCallback {
    int ERROR = 1;
    int LOG = 2;
    int COMPLETE = 3;
    int FAILED = 4;
    int TIMEOUT = 5;

    void call(int status, String line);
}
