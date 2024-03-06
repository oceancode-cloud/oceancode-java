/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.list;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author qinjiawang
 */
public class LongList extends ArrayList<Long> {
    public LongList() {
    }

    public LongList(Long element) {
        add(element);
    }

    public LongList(Collection<Long> collection) {
        addAll(collection);
    }
}
