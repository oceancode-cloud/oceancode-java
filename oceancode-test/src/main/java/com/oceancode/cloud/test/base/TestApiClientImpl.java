package com.oceancode.cloud.test.base;

import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.common.ApiClientImpl;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.HashMap;
import java.util.Map;

public class TestApiClientImpl extends ApiClientImpl {
    public static final ApiClient API_CLIENT = new TestApiClientImpl();


    @Override
    public Map<String, String> getHeaderParams() {
        Map<String, String> map = new HashMap<>();
        if (ValueUtil.isNotEmpty(BaseTest.getToken())) {
            map.put("Authorization", "Bearer " + BaseTest.getToken());
        }

        return map;
    }
}
