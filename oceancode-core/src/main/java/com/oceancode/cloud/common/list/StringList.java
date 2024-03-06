/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.list;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author qinjiawang
 */
public class StringList extends ArrayList<String> {
    public StringList() {
    }

    public StringList(String element) {
        add(element);
    }

    public StringList(Collection<String> collection) {
        addAll(collection);
    }
}
