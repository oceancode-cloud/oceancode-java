package com.oceancode.cloud.common.excel;

import com.oceancode.cloud.api.file.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ByteRow implements Row {
    private byte[] array;
    private String text;
    private byte[] leftBytes;
    private List<Object> values;
    private int partIndex;

    public ByteRow(byte[] contents) {
        this.array = contents;
    }

    @Override
    public int getSize() {
        return getValues().size();
    }

    @Override
    public List<Object> getValues() {
        if (Objects.nonNull(values)) {
            return values;
        }
        values = new ArrayList<>();
        values.add(getText());
        return values;
    }

    @Override
    public Object getValue(int index) {
        List<Object> list = getValues();
        if (index <= 0 || index > list.size()) {
            return null;
        }
        return list.get(index);
    }

    @Override
    public Object getValue() {
        return array;
    }

    @Override
    public String getText() {
        if (Objects.nonNull(values)) {
            return text;
        }
        byte last = this.array[array.length - 1];
        if (last != 10) {
            int i = array.length - 1;
            for (; i >= 0; i--) {
                if (array[i] == 10) {
                    break;
                }
            }
            i++;
            leftBytes = new byte[array.length - i];
            System.arraycopy(array, i, leftBytes, 0, leftBytes.length);
        }

        text = new String(array);
        if (last != 10) {
            text = text.substring(0, text.lastIndexOf((char) 10));
        }

        return text;
    }

    @Override
    public int getIndex() {
        return partIndex;
    }

    public byte[] getLeftBytes() {
        return leftBytes;
    }
}
