package com.oceancode.cloud.test.base;

import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import jakarta.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTest extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    private Integer port;

    @Resource
    private CommonConfig commonConfig;

    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal();

    protected CommonConfig getConfig() {
        return commonConfig;
    }

    public static String getToken() {
        if (Objects.isNull(CONTEXT.get())) {
            return null;
        }
        Object token = CONTEXT.get().get("token");
        if (Objects.isNull(token)) {
            return null;
        }
        return token + "";
    }


    @BeforeClass
    public void initContext() {
        CONTEXT.set(new HashMap<>());
    }

    @AfterClass
    public void removeContext() {
        CONTEXT.remove();
    }

    protected void setEnvVariable(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    protected Object getEnvVariable(String key) {
        return CONTEXT.get().get(key);
    }

    protected String getBaseUrl() {
        return "http://127.0.0.1:" + getPort();
    }

    protected String buildUrl(String url) {
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return getBaseUrl() + url;
    }


    protected Integer getPort() {
        return port;
    }
}
