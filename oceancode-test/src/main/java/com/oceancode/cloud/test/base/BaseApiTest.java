package com.oceancode.cloud.test.base;

import com.oceancode.cloud.api.ApiClient;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseApiTest extends BaseTest {
    @Resource
    protected ApiClient apiClient;

    protected ApiClient apiClient() {
        return apiClient;
    }


    protected void setToken(String token) {
        setEnvVariable("Authorization", token);
    }

    protected void setJSessionId(String value) {
        setEnvVariable("jsessionId", value);
    }
}
