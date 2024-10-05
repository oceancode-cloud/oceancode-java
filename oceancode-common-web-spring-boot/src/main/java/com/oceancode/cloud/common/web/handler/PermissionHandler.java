package com.oceancode.cloud.common.web.handler;

import com.oceancode.cloud.api.ApplicationLifeCycleService;
import com.oceancode.cloud.api.permission.ResourcePermissionService;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.UserBaseInfo;
import com.oceancode.cloud.api.session.UserType;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.PermissionUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class PermissionHandler implements ApplicationLifeCycleService {

    private static ResourcePermissionService resourcePermissionService;
    private static SessionService sessionService;

    @Override
    public void onReady() {
        try {
            resourcePermissionService = ComponentUtil.getBean(ResourcePermissionService.class);
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
        if (permission.resourceType() != PermissionConst.RESOURCE_TYPE_QUERY && permission.resourceType() != PermissionConst.RESOURCE_TYPE_ANY) {
            UserBaseInfo userBaseInfo = sessionService.getUserInfo(token);
            if (Objects.nonNull(userBaseInfo) && UserType.EXAMPLE.equals(userBaseInfo.getUserType())) {
                return false;
            }
        }

        int count = 0;
        for (String authority : authorities) {
            if (PermissionConst.AUTHORITY_LOGIN.equals(authority)) {
                if (ValueUtil.isEmpty(token)) {
                    return false;
                }
                if (!sessionService.isLogin(token)) {
                    throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
                }
                checkedLoginAuth = true;
                count++;
            } else if (PermissionConst.AUTHORITY_UN_LOGIN.equals(authority)) {
                if (SessionUtil.userId() != null) {
                    return false;
                }
                checkedLoginAuth = true;
                count++;
            } else if (PermissionConst.PRIVATE_TOKEN.equals(authority)) {
                String privateToken = ApiUtil.getPrivateToken();
                if (ValueUtil.isEmpty(privateToken)) {
                    return false;
                }
                return PermissionUtil.checkPermission(permission);
            }
        }

        if (!checkedLoginAuth) {
            if (!sessionService.isLogin(token)) {
                throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
            }
        }

        if (resourcePermissionService != null) {
            return resourcePermissionService.permission(permission);
        }

        if (permission.authorities().length > count) {
            return PermissionUtil.checkPermission(permission);
        }

        return true;
    }
}
