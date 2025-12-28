package com.oceancode.cloud.common.list;

import java.util.ArrayList;
import java.util.Collection;

public class WrapperArrayList<T> extends ArrayList<T> {
    public WrapperArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public WrapperArrayList() {
    }

    public WrapperArrayList(Collection<? extends T> c) {
        super(c);
    }
}
