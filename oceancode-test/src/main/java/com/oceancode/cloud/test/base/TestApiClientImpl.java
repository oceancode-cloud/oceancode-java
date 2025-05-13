package com.oceancode.cloud.test.base;

import com.oceancode.cloud.common.ApiClientImpl;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class TestApiClientImpl extends ApiClientImpl {

    public TestApiClientImpl(ApplicationContext applicationContext) {
        super(applicationContext);

    }


    @Override
    protected void processHeader(String uri, WebClient.RequestHeadersSpec<?> headersSpec) {
        if (ValueUtil.isEmpty(BaseTest.getToken())) {
            return;
        }
        headersSpec.header("Authorization", "Bearer " + BaseTest.getToken());
    }

    @Override
    protected String getUrl(String url) {
        String result = super.getUrl(url);
        if (!result.startsWith("http:/") && !result.startsWith("https:/")) {
            if (!result.startsWith("/")) {
                result = "/" + result;
            }
            result = BaseTest.getBaseUrl() + result;
        }
        return result;
    }
}
