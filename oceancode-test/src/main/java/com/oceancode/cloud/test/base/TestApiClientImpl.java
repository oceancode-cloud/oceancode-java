package com.oceancode.cloud.test.base;

import com.oceancode.cloud.common.ApiClientImpl;
import com.oceancode.cloud.common.util.ValueUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class TestApiClientImpl extends ApiClientImpl {

    @Override
    protected void processHeader(String uri, WebClient.RequestHeadersSpec<?> headersSpec) {
        if (ValueUtil.isEmpty(BaseTest.getToken())) {
            return;
        }
        headersSpec.header("Authorization", "Bearer " + BaseTest.getToken());
    }
}
