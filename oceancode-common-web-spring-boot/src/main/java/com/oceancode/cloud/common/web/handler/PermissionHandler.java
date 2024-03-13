package com.oceancode.cloud.common.web.handler;

import com.oceancode.cloud.api.ApplicationLifeCycleService;
import com.oceancode.cloud.api.permission.ApiPermissionService;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.PermissionUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionHandler implements ApplicationLifeCycleService {

    private static ApiPermissionService apiPermissionService;
    private static SessionService sessionService;

    @Override
    public void onReady() {
        try {
            apiPermissionService = ComponentUtil.getBean(ApiPermissionService.class);
        } catch (Exception e) {
            // ignore
        }

        try {
            sessionService = ComponentUtil.getBean(SessionService.class);
        } catch (Exception e) {
            // ignore
        }
    }

    @Pointcut("@annotation(com.oceancode.cloud.api.permission.Permission)")
    public void permissionPointCut() {

    }

    @Around("permissionPointCut()")
    public Object permissionCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        Permission permission = method.getAnnotation(Permission.class);
        if (doCheckPermission(permission)) {
            return proceedingJoinPoint.proceed();
        } else {
            throw new BusinessRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }
    }

    private boolean doCheckPermission(Permission permission) {
        String[] authorities = permission.authorities();
        boolean checkedLoginAuth = false;

        String token = ApiUtil.getToken();
        for (String authority : authorities) {
            if (PermissionConst.AUTHORITY_LOGIN.equals(authority)) {
                if (!sessionService.isLogin(token)) {
                    throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
                }
                checkedLoginAuth = true;
            } else if (PermissionConst.AUTHORITY_UN_LOGIN.equals(authority)) {
                if (SessionUtil.userId() != null) {
                    return false;
                }
                checkedLoginAuth = true;
            }
        }

        if (!checkedLoginAuth) {
            if (!sessionService.isLogin(token)) {
                throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
            }
        }

        if (apiPermissionService != null) {
            return apiPermissionService.permission(permission);
        }

        if (permission.authorities().length > 0) {
            return PermissionUtil.checkPermission(permission);
        }

        return true;
    }
}
