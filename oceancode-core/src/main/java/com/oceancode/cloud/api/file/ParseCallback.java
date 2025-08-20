package com.oceancode.cloud.api.file;

public interface ParseCallback {
    boolean parse(FileContext context, Row row);

    boolean match(FileContext context);
}
