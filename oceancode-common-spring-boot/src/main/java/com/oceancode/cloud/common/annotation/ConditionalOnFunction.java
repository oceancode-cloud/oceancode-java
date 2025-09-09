package com.oceancode.cloud.common.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConditionalOnFunction implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (!MethodMetadata.class.isInstance(metadata)) {
            return false;
        }
        List<String> allFunctions = new ArrayList<>();
        MethodMetadata methodMetadata = MethodMetadata.class.cast(metadata);
        for (MergedAnnotation<Annotation> annotation : methodMetadata.getAnnotations()) {
            for (Class<? extends Annotation> metaType : annotation.getMetaTypes()) {
                if (!metaType.toString().endsWith(" " + ConditionalOnClass.class.getName())) {
                    continue;
                }
                Optional<Object> value = annotation.getValue("value");
                if (!value.isPresent()) {
                    continue;
                }
                Object object = value.get();
                if (object instanceof Class<?>[] classes) {
                    for (Class<?> clazz : classes) {
                        allFunctions.add(clazz.getName());
                    }
                }
            }
        }

        String returnTypeName = methodMetadata.getReturnTypeName();
        for (String beanDefinitionName : context.getRegistry().getBeanDefinitionNames()) {
            if (!context.getRegistry().isBeanNameInUse(beanDefinitionName)) {
                continue;
            }
            BeanDefinition beanDefinition = context.getRegistry().getBeanDefinition(beanDefinitionName);
            if (beanDefinition instanceof ScannedGenericBeanDefinition scannedGenericBeanDefinition) {
                AnnotationMetadata annotationMetadata = scannedGenericBeanDefinition.getMetadata();
                String className = annotationMetadata.getClassName();
                boolean isDefaultFunctionImpl = className.endsWith("FunctionImpl") &&
                        className.contains(".Default");

                if (isDefaultFunctionImpl) {
                    continue;
                }
                if (Objects.equals(returnTypeName, annotationMetadata.getSuperClassName())) {
                    return false;
                }


                for (String interfaceName : annotationMetadata.getInterfaceNames()) {
                    if (allFunctions.contains(interfaceName)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
