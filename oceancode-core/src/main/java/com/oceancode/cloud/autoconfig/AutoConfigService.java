package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;

public interface AutoConfigService {
    AutoConfigResponse autoConfig(AutoConfigRequest autoConfigRequest);
}
