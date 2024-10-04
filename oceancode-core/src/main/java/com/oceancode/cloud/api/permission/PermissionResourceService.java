package com.oceancode.cloud.api.permission;

import java.util.Set;

public interface PermissionResourceService {
    boolean checkPermission(Permission permission, String authority);

    Set<String> getPermissions(String resourceId);
}
