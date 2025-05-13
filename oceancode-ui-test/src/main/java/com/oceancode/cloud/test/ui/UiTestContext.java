package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.test.TestContext;
import com.oceancode.cloud.test.ui.component.impl.VxeTree;
import com.oceancode.cloud.test.ui.container.UiUtil;

public class UiTestContext extends TestContext {

    @Override
    public <T> T get(String key, boolean required) {
        Object value = super.get(key, required);
        if (value instanceof VxeTree) {
            UiUtil.get(() -> ((VxeTree) value).expandAll());
        }
        return (T) value;
    }
}
