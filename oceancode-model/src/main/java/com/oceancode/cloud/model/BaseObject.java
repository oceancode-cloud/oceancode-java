package com.oceancode.cloud.model;

import com.oceancode.cloud.api.UnSerializable;
import com.oceancode.cloud.api.model.MObject;
import com.oceancode.cloud.common.util.ValueUtil;

public interface BaseObject<T extends MObject> extends UnSerializable {
    T object();

    String id();

    default Long idAsLong() {
        return ValueUtil.isNotEmpty(id()) ? Long.parseLong(id()) : null;
    }

    String versionId();

    boolean isPersist();

    boolean isNull();

    boolean isEmpty();
}
