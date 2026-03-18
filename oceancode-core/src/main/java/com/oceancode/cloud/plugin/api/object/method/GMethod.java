package com.oceancode.cloud.plugin.api.object.method;

import com.oceancode.cloud.plugin.api.object.GDescription;
import com.oceancode.cloud.plugin.api.object.GName;
import com.oceancode.cloud.plugin.api.object.GTypeObject;

public class GMethod<T> extends GTypeObject<T> implements GName, GDescription {

    public GMethod(T object) {
        super(object);
    }
}
