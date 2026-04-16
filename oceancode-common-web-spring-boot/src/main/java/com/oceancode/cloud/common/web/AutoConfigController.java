package com.oceancode.cloud.common.web;

import com.oceancode.cloud.autoconfig.AutoConfigRequest;
import com.oceancode.cloud.autoconfig.AutoConfigResponse;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.autoconfig.AutoConfigService;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ResultData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
@ConditionalOnBean(AutoConfigService.class)
public class AutoConfigController {
    private AutoConfigService autoConfigService;

    public AutoConfigController(AutoConfigService autoConfigService) {
        this.autoConfigService = autoConfigService;
    }

    @Permission(resourceId = "autoConfig", authorities = {PermissionConst.AUTHORITY_LOGIN})
    @PostMapping("/autoConfig")
    public ResultData<?> autoConfig2(@RequestBody AutoConfigRequest autoConfigRequest) {
        AutoConfigResponse result = autoConfigService.autoConfig(autoConfigRequest);
        return ResultData.isOk(result);
    }
}
