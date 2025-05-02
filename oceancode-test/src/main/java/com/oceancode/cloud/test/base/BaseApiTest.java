package com.oceancode.cloud.test.base;

import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseApiTest extends BaseTest {
    @Resource
    protected TestApiClientImpl apiClient;

    protected ApiClient apiClient() {
        return apiClient;
    }

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
    }

    protected void setToken(String token) {
        if (ValueUtil.isNotEmpty(token)) {
            setEnvVariable("Authorization", token);
        }
    }

    protected void setJSessionId(String value) {
        setEnvVariable("jsessionId", value);
    }
}
