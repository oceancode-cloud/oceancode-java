package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.permission.PermissionResourceService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.Objects;

public final class PermissionUtil {
    private static CommonConfig commonConfig;
    private static PermissionResourceService permissionResourceService;

    private PermissionUtil() {

    }

    static {
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
        try {
            permissionResourceService = ComponentUtil.getBean(PermissionResourceService.class);
        } catch (Exception e) {
            // ignore
        }
    }

    public static String[] getAuthorities(String resourceId) {
        String str = commonConfig.getValue(Config.Permission.PREFIX + resourceId + "." + Config.Permission.AUTHORITIES);
        if (str == null) {
            return null;
        }
        return str.split(",");
    }

    public static String getOperator(String resourceId) {
        return commonConfig.getValue(Config.Permission.PREFIX + resourceId + "." + Config.Permission.OPERATOR, PermissionConst.OPERATION_OR);
    }

    public static boolean checkPrivateToken(Permission permission) {
        if (Objects.isNull(permissionResourceService)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, PermissionResourceService.class.getName() + " not found implementation.");
        }
        return permissionResourceService.checkPermission(permission, PermissionConst.PRIVATE_TOKEN);
    }

    public static boolean checkPermission(Permission permission) {
        boolean isAnd = PermissionConst.OPERATION_OR.equals(getOperator(permission.resourceId())) || PermissionConst.OPERATION_OR.equals(permission.operation());
        String[] authorities = permission.authorities();
        if (authorities.length == 0) {
            authorities = getAuthorities(permission.resourceId());
        }

        int matchCount = 0;
        for (String authority : authorities) {
            if (PermissionConst.AUTHORITY_LOGIN.equals(authority) ||
                    PermissionConst.AUTHORITY_UN_LOGIN.equals(authority)) {
                matchCount++;
                continue;
            }
            if (permissionResourceService.checkPermission(permission, authority)) {
                matchCount++;
                if (!isAnd) {
                    return true;
                }
            }
        }
        return matchCount == authorities.length;
    }

}
