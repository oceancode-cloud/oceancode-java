package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.test.ui.container.UiUtil;
import org.junit.jupiter.api.AfterAll;

public class BaseUiTest {
    public BaseUiTest() {
    }

    @AfterAll
    public static void destroy() {
        UiUtil.getPage().close();
    }
}
