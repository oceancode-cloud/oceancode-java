package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.common.util.JsonUtil;

public class DefaultAutoConfigContext extends AutoConfigContext {
    public DefaultAutoConfigContext(AutoConfigRequest request) {
        super(request);
    }

    public void toAdd(Object value) {
        super.toAdd(JsonUtil.toJson(value));
    }

    public void toUpdate(Object value) {
        super.toUpdate(JsonUtil.toJson(value));
    }

    public void toDelete(Object value) {
        super.toDelete(JsonUtil.toJson(value));
    }
}
