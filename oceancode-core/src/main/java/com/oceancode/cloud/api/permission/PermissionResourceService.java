package com.oceancode.cloud.api.permission;

import java.util.Set;

public interface PermissionResourceService {
    boolean checkPerssion(Permission permission,String authority);
}
