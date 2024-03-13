package com.oceancode.cloud.common.annotation;

import com.oceancode.cloud.annotation.Inject;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

//@Configuration
public class InjectProcessor {

    @Bean
    public FieldRetrievingFactoryBean fieldRetrievingFactoryBean() throws IllegalAccessException {
        FieldRetrievingFactoryBean bean = new FieldRetrievingFactoryBean();
        bean.setTargetObject(getStaticFields(Inject.class));
        return bean;
    }

    private Object getStaticFields(Class<?> annotationClass) throws IllegalAccessException {
        Map<String, Object> staticFields = new HashMap<>();
        Field[] fields = annotationClass.getFields();
        for (Field field : fields) {
            Inject value = AnnotationUtils.findAnnotation(field, Inject.class);
            if (value == null) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "inject error");
            }
        }
        return staticFields;
    }
}
