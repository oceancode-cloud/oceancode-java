package com.oceancode.cloud.plugin.api.model;

import com.oceancode.cloud.plugin.api.GDescription;
import com.oceancode.cloud.plugin.api.GName;
import com.oceancode.cloud.plugin.api.GObject;

import java.util.List;

public interface GModel<T> extends GObject<T>, GDescription, GName {
    String modelId();

    boolean isParam();

    default String path() {
        return path(true);
    }

    String path(boolean isFullPath);

    default List<GModelField<?>> fields() {
        return fields(false);
    }

    List<GModelField<?>> fields(boolean isAll);

    List<GModelField<?>> refFields();

    boolean isInput();

    boolean isOutput();

    boolean isDomain();
}
