package com.oceancode.cloud.api.permission;

public interface PermissionResourceService {
    boolean checkPermission(Permission permission, String authority);
}
