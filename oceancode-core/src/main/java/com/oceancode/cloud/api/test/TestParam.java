package com.oceancode.cloud.api.test;

import com.oceancode.cloud.api.tool.ToolParam;

import java.util.HashMap;
import java.util.Map;

public class TestParam extends ToolParam {
    public TestParam(Map<String, Object> map) {
        super(map);
    }

    public TestParam() {
        super(new HashMap<>());
    }

}
