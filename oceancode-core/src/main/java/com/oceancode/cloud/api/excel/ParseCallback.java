package com.oceancode.cloud.api.excel;

public interface ParseCallback {
    boolean parse(FileContext context, Row row);

    boolean match(FileContext context);
}
