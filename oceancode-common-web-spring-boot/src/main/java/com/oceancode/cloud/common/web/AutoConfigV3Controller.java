package com.oceancode.cloud.common.web;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.autoconfig.AutoConfigRule;
import com.oceancode.cloud.autoconfig.AutoConfigService;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ResultData;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
@ConditionalOnBean(AutoConfigRule.class)
public class AutoConfigV3Controller {
    @Resource
    private AutoConfigService autoConfigService;

    @Permission(resourceId = "autoConfig2", authorities = {PermissionConst.AUTHORITY_LOGIN})
    @PostMapping("/v2/autoConfig")
    public ResultData<?> autoConfig2(@RequestBody AutoConfigRequest autoConfigRequest) {
        AutoConfigResponse result = autoConfigService.autoConfig(autoConfigRequest);
        return ResultData.isOk(result);
    }
}
