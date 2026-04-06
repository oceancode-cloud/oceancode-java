package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;

public abstract class AbstractAutoConfigService implements AutoConfigService {

    @Override
    public AutoConfigResponse autoConfig(AutoConfigRequest request) {
        AutoConfigResponse response = new AutoConfigResponse();
        doAutoConfig(request, response);
        return response;
    }

    protected abstract void doAutoConfig(AutoConfigRequest request, AutoConfigResponse response);
}
