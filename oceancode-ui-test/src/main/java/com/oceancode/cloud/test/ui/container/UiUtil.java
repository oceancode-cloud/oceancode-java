package com.oceancode.cloud.test.ui.container;

import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;

public final class UiUtil {
    private UiUtil() {
    }

    public static String getFrameworkTag() {
        return ComponentUtil.getBean(CommonConfig.class).getValue("oc.test.ui.framwork.tag", "el");
    }
}
