package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.permission.PermissionResourceService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;

import java.util.Collections;
import java.util.Set;

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


    public static boolean checkPermission(Permission permission) {
        boolean isAnd = PermissionConst.OPERATION_OR.equals(getOperator(permission.resourceId())) || PermissionConst.OPERATION_OR.equals(permission.operation());
        String[] authorities = permission.authorities();
        if (authorities.length == 0) {
            authorities = getAuthorities(permission.resourceId());
        }

        Set<String> userAuthorities = null == permissionResourceService ? Collections.emptySet() : permissionResourceService.getAuthorities(permission.resourceId());
        if (ValueUtil.isEmpty(userAuthorities) && authorities.length > 0) {
            return false;
        }
        int matchCount = 0;
        for (String authority : authorities) {
            if (PermissionConst.AUTHORITY_LOGIN.equals(authority) ||
                    PermissionConst.AUTHORITY_UN_LOGIN.equals(authority)) {
                matchCount++;
                continue;
            }
            if (userAuthorities.contains(authority)) {
                matchCount++;
                if (!isAnd) {
                    return true;
                }
            }
        }
        return matchCount == authorities.length;
    }

}
