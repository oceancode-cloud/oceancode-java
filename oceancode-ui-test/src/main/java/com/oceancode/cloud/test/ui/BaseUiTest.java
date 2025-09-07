package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.test.TestContextManager;
import com.oceancode.cloud.test.ui.container.UiUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@ExtendWith(UiReporterTestExecutionListener.class)
public class BaseUiTest {
    private static final InheritableThreadLocal<UiTestContextManager> CONTEXT = new InheritableThreadLocal<>();
    protected static CommonConfig commonConfig = new UiCommonConfig(null);

    public BaseUiTest() {
    }

    @BeforeAll
    public static void setup() {
        CONTEXT.set(new UiTestContextManager());
    }

    @AfterAll
    public static void destroy() {
        CONTEXT.remove();
        UiUtil.getPage().close();
    }

    public static TestContextManager getContext() {
        return CONTEXT.get();
    }
//
//    @DynamicPropertySource
//    private static void setProperties(DynamicPropertyRegistry registry){
//
//    }
}
