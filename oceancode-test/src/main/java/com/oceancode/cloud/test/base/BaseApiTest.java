package com.oceancode.cloud.test.base;

import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.util.ComponentUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseApiTest extends BaseTest {

    protected ApiClient apiClient() {
        return TestApiClientImpl.API_CLIENT;
    }

    protected void setToken(String token) {
        setEnvVariable("token", token);
    }

    protected void setJSessionId(String value) {
        setEnvVariable("jsessionId", value);
    }
}
