package com.oceancode.cloud.test.base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BaseFunctionTest extends BaseTest {

    @BeforeAll
    public static void init() {
        BaseTest.initContext();
        TestHttpServletRequest request = new TestHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterAll
    public static void destroy() {
        RequestContextHolder.resetRequestAttributes();
        BaseTest.removeContext();
        ;
    }
}
