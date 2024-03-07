/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common;

import com.oceancode.cloud.api.ApplicationLifeCycleService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Order(1)
@Component
@Primary
public class CommandLineRunnerInit implements CommandLineRunner {
    @Resource
    private CommonConfig commonConfig;

    @Override
    public void run(String... args) throws Exception {
        doCheckConfig();
        for (ApplicationLifeCycleService service : ComponentUtil.getBeans(ApplicationLifeCycleService.class).values()) {
            service.onReady();
        }
    }

    public void doCheckConfig() {
        commonConfig.getStripPrefixes();
    }
}
