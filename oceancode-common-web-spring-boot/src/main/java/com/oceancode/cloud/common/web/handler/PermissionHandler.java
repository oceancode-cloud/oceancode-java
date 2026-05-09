package com.oceancode.cloud.common.web.handler;

import com.oceancode.cloud.api.ApplicationLifeCycleService;
import com.oceancode.cloud.api.interceptor.FunctionInterceptor;
import com.oceancode.cloud.api.permission.ResourcePermissionService;
import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.session.RoleType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class PermissionHandler implements ApplicationLifeCycleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionHandler.class);

    private static ResourcePermissionService resourcePermissionService;
    private static SessionService sessionService;
    @Autowired(required = false)
    FunctionInterceptor functionInterceptor;

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

        boolean isSse = PermissionConst.RESOURCE_SSE == permission.resourceType();
        if (isSse) {
            try {
                return processDoPermissionCheck(permission, proceedingJoinPoint);
            } catch (Exception e) {
                LOGGER.error("error", e);
                return ResponseEntity.badRequest().build();
            }
        }
        return processDoPermissionCheck(permission, proceedingJoinPoint);
    }

    private Object processDoPermissionCheck(Permission permission, ProceedingJoinPoint proceedingJoinPoint) {
        String token = ApiUtil.getToken();
        if (sessionService.isLogin(token)) {
            if (Objects.isNull(SessionUtil.getUserInfo())) {
                SessionUtil.setUserinfo(sessionService.getUserInfo(token));
            }
        }
        if (Objects.nonNull(functionInterceptor)) {
            functionInterceptor.before(permission.resourceId(), permission.resourceType());
        }
        boolean ret = doCheckPermission(permission) && PermissionUtil.checkCustomPermission(permission);
        if (!ret) {
            ret = PermissionUtil.checkPrivateToken(permission);
        }
        if (ret) {
            Object proceed = null;
            try {
                proceed = proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
            } finally {
                if (Objects.nonNull(functionInterceptor)) {
                    functionInterceptor.after(permission.resourceId(), permission.resourceType());
                }
            }

            return proceed;
        } else {
            throw new BusinessRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }
    }

    private boolean doCheckPermission(Permission permission) {
        String[] authorities = permission.authorities();
        boolean checkedLoginAuth = false;


        String token = ApiUtil.getToken();
        if (permission.resourceType() != PermissionConst.RESOURCE_TYPE_QUERY && permission.resourceType() != PermissionConst.RESOURCE_TYPE_ANY) {
            if (ValueUtil.isEmpty(token)) {
                return false;
            }
            UserBaseInfo userBaseInfo = sessionService.getUserInfo(token);
            if (Objects.nonNull(userBaseInfo) && UserType.EXAMPLE.equals(userBaseInfo.getUserType())) {
                return false;
            }
        }

        boolean isLogin = sessionService.isLogin(token);

        int count = 0;
        RoleType userRole = SessionUtil.getUserRole();
        for (String authority : authorities) {
            if (PermissionConst.AUTHORITY_LOGIN.equals(authority)) {
                if (ValueUtil.isEmpty(token)) {
                    return false;
                }
                if (!isLogin) {
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
            if (!isLogin) {
                throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
            }
        }

        if (permission.authorities().length > count) {
            return PermissionUtil.checkPermission(permission);
        }

        return true;
    }
}
