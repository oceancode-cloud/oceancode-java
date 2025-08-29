package com.oceancode.cloud.test;

import com.oceancode.cloud.common.PluginLoadingInitializer;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.test.annotation.CaseId;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestPluginLoadingInitializer extends PluginLoadingInitializer {
    public final static Map<String, Map<String, Method>> CASES = new HashMap<>();

    @Override
    protected boolean processRegisterBean(Class<?> clazz, Class<?> func, BeanDefinitionRegistry registry) {
        boolean ret = super.processRegisterBean(clazz, clazz, registry);
        if (TestFunction.class.isAssignableFrom(clazz)) {
            CaseId groupCaseId = clazz.getAnnotation(CaseId.class);
            if (Objects.nonNull(groupCaseId)) {
                if (!CASES.containsKey(groupCaseId.value())) {
                    CASES.put(groupCaseId.value(), new HashMap<>());
                }
                Map<String, Method> map = CASES.get(groupCaseId.value());
                for (Method declaredMethod : clazz.getDeclaredMethods()) {
                    CaseId caseId = declaredMethod.getAnnotation(CaseId.class);
                    if (Objects.isNull(caseId)) {
                        continue;
                    }
                    if (map.containsKey(caseId.value())) {
                        throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "caseId(" + caseId.value() + ") already exists in" + clazz.getName() + " " + declaredMethod.getName());
                    }
                    map.put(caseId.value(), declaredMethod);
                }
            }
        }
        return ret;
    }
}
