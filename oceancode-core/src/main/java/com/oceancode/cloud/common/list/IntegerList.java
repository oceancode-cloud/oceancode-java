/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.list;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author qinjiawang
 */
public class IntegerList extends ArrayList<Integer> {
    public IntegerList() {
    }

    public IntegerList(Integer element) {
        add(element);
    }

    public IntegerList(Collection<Integer> collection) {
        addAll(collection);
    }
}
