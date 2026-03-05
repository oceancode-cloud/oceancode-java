package com.oceancode.cloud.api.permission;

import java.util.Set;

public interface PermissionResourceService {
    boolean checkPermission(String resourceId, int resourceType, String authority);

    Set<String> getPermissions(String resourceId, int resourceType);
}
