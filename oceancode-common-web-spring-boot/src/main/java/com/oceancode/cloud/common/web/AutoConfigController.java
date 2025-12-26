package com.oceancode.cloud.common.web;

import com.oceancode.cloud.api.autoconfig.AutoConfigHandler;
import com.oceancode.cloud.api.autoconfig.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.AutoConfigRequestUtil;
import com.oceancode.cloud.api.autoconfig.AutoConfigResult;
import com.oceancode.cloud.api.autoconfig.AutoConfigService;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ResultData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
@ConditionalOnBean(AutoConfigService.class)
public class AutoConfigController {
    private AutoConfigService autoConfigService;

    public AutoConfigController(Set<AutoConfigHandler> handlers, AutoConfigService autoConfigService) {
        this.autoConfigService = autoConfigService;
        for (AutoConfigHandler handler : handlers) {
            AutoConfigRequestUtil.registerHandler(handler);
        }
    }

    @Permission(resourceId = "autoConfig", authorities = {PermissionConst.AUTHORITY_LOGIN})
    @PostMapping("/autoConfig")
    public ResultData<AutoConfigResult> autoConfig(@RequestBody AutoConfigRequest autoConfigRequest) {
        return ResultData.isOk(autoConfigService.autoConfig(autoConfigRequest));
    }
}
