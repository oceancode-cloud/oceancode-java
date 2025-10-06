package com.oceancode.cloud.common.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.MethodMetadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
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
                        if (context.getRegistry().isBeanNameInUse(clazz.getName())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
