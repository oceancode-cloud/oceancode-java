package com.oceancode.cloud.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    String name() default "";

    Class<?> returnType() default Void.class;
}
