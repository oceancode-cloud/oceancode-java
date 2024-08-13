package com.oceancode.cloud.test.base;

import org.springframework.web.context.request.FacesRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseFunctionTest extends BaseTest {

    @BeforeClass
    public void init() {
        TestHttpServletRequest request = new TestHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterClass
    public void destroy() {
        RequestContextHolder.resetRequestAttributes();
    }
}
