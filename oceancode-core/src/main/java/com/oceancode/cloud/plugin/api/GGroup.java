package com.oceancode.cloud.plugin.api;

import java.util.List;

public interface GGroup<T> extends GObject<T>, GName, GDescription {
    GGroup<T> parent();

    List<GGroup<T>> parents();

}
