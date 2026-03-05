package com.oceancode.cloud.common.web;

import com.oceancode.cloud.api.autoconfig.AutoConfigHandler;
import com.oceancode.cloud.api.autoconfig.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.AutoConfigRequestUtil;
import com.oceancode.cloud.api.autoconfig.AutoConfigResult;
import com.oceancode.cloud.api.autoconfig.AutoConfigRule;
import com.oceancode.cloud.api.autoconfig.AutoConfigService;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Set;

//@RestController
//@RequestMapping(CommonConst.API_PREFIX)
//@ConditionalOnBean(com.oceancode.cloud.api.autoconfig.v2.AutoConfigRule.class)
public class AutoConfigV2Controller {
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoConfigRequestUtil.class);
    private com.oceancode.cloud.api.autoconfig.v2.AutoConfigService autoConfigService2;

    public AutoConfigV2Controller(com.oceancode.cloud.api.autoconfig.v2.AutoConfigService autoConfigService2) {
        this.autoConfigService2 = autoConfigService2;
    }

    @Permission(resourceId = "autoConfig1", authorities = {PermissionConst.AUTHORITY_LOGIN})
    @PostMapping("/v1/autoConfig")
    public ResultData<?> autoConfig2(@RequestBody com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest autoConfigRequest) {
        AutoConfigResponse autoConfigResponse = autoConfigService2.autoConfig(autoConfigRequest);
        return ResultData.isOk(autoConfigResponse);
    }
}
