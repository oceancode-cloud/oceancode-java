package com.oceancode.cloud.plugin.api.model;

import com.oceancode.cloud.plugin.api.GObject;

public interface GModelField<T> extends GObject<T> {
    GModel<?> model();

    boolean isPrimaryKey();

    boolean required();

    GModel<?> isRef();
}
