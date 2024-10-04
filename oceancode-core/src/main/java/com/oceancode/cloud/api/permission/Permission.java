package com.oceancode.cloud.api.permission;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {
    String resourceId() default "";

    String operation() default PermissionConst.OPERATION_OR;

    String[] authorities();

    int resourceType() default PermissionConst.RESOURCE_TYPE_ANY;
}
