package com.oceancode.cloud.api.file;

import java.util.List;

public interface Row {
    int getSize();

    List<Object> getValues();

    Object getValue(int index);

    Object getValue();

    String getText();

    int getIndex();
}
