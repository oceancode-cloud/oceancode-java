package com.oceancode.cloud.plugin.api.method;

import com.oceancode.cloud.plugin.api.GDescription;
import com.oceancode.cloud.plugin.api.GName;
import com.oceancode.cloud.plugin.api.GObject;
import com.oceancode.cloud.plugin.api.GPackage;
import com.oceancode.cloud.plugin.api.model.GModel;

public interface GMethod<T> extends GObject<T>, GName, GDescription {
    String method();

    boolean hasInput();

    GModel<?> input();

    boolean hasReturn();

    GModel<?> result();

    boolean isReturnList();

    GPackage<?> pkg();
}
