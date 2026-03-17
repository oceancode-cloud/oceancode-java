package com.oceancode.cloud.plugin.api.method;

import com.oceancode.cloud.plugin.api.GObject;

public interface GMethodContent extends GObject<Object> {
    MethodContentResponse resolveMethodContent(String code);
}
