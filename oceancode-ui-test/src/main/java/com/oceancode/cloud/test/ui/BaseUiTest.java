package com.oceancode.cloud.test.ui;

import com.oceancode.cloud.test.TestContextManager;
import com.oceancode.cloud.test.ui.container.UiUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class BaseUiTest {
    private static final ThreadLocal<TestContextManager> CONTEXT = new ThreadLocal<>();

    public BaseUiTest() {
    }

    @BeforeAll
    public static void setup() {
        CONTEXT.set(new TestContextManager());
    }

    @AfterAll
    public static void destroy() {
        CONTEXT.remove();
        UiUtil.getPage().close();
    }

    public TestContextManager getContext() {
        return CONTEXT.get();
    }
//
//    @DynamicPropertySource
//    private static void setProperties(DynamicPropertyRegistry registry){
//
//    }
}
