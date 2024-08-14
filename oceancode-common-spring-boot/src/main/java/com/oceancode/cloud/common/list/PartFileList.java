package com.oceancode.cloud.common.list;

import com.oceancode.cloud.common.entity.PartFile;

import java.util.ArrayList;
import java.util.Collection;

public class PartFileList extends ArrayList<PartFile> {

    public PartFileList(int initialCapacity) {
        super(initialCapacity);
    }

    public PartFileList() {
    }

    public PartFileList(Collection<? extends PartFile> c) {
        super(c);
    }
}
