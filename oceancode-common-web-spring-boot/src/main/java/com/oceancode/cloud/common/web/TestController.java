package com.oceancode.cloud.common.web;

import com.oceancode.cloud.api.test.TestFunction;
import com.oceancode.cloud.api.test.TestParam;
import com.oceancode.cloud.api.test.TestResult;
import com.oceancode.cloud.autoconfig.AutoConfigService;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.util.ValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
@ConditionalOnBean(TestFunction.class)
//@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "test")
public class TestController {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    private Map<String, TestFunction> testFunctionMap;

    public TestController(Set<TestFunction> testFunctionList) {
        testFunctionMap = new HashMap<>(testFunctionList.size());
        for (TestFunction testFunction : testFunctionList) {
            if (ValueUtil.isEmpty(testFunction.getCaseId())) {
                continue;
            }
            if (testFunctionMap.containsKey(testFunction.getCaseId())) {
                LOGGER.error("test case function alread exists,caseId:{},name:{},description:{},{}", testFunction.getCaseId(), testFunction.getName(), testFunction.getDescription(), testFunction.getClass().getName());
                continue;
            }
            testFunctionMap.put(testFunction.getCaseId(), testFunction);
        }
    }

    @PostMapping("test/{caseId}")
    public ResultData<Object> execute(@PathVariable("caseId") String caseId, @RequestBody TestParam param) {
        TestFunction testFunction = testFunctionMap.get(caseId);
        if (Objects.isNull(testFunction)) {
            return ResultData.isFail().message("case id invalid");
        }
        if (Objects.isNull(param)) {
            param = new TestParam(Collections.emptyMap());
        }
        TestResult testResult = new TestResult();
        try {
            testFunction.execute(param, testResult);
        } catch (Exception e) {
            return ResultData.isFail().data(e);
        }
        return ResultData.isOk(testResult);
    }
}
