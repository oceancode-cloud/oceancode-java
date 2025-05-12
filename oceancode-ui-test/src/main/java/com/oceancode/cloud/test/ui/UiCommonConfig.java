package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.common.config.CommonConfig;
import org.springframework.context.ApplicationContext;

public class UiCommonConfig extends CommonConfig {
    public UiCommonConfig(ApplicationContext applicationContext) {
        super(applicationContext);
        environment = new UiEnvironmentImpl();
    }
}
