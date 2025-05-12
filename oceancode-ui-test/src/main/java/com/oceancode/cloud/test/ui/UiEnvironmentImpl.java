package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.test.ui.container.UiUtil;
import org.springframework.core.env.StandardEnvironment;

public class UiEnvironmentImpl extends StandardEnvironment {

    @Override
    public String getProperty(String key) {
        return UiUtil.getProperties().getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return UiUtil.getProperties().getProperty(key, defaultValue);
    }
}
