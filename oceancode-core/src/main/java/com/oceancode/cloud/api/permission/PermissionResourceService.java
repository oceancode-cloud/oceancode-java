package com.oceancode.cloud.api.permission;

import java.util.Set;

public interface PermissionResourceService {

    Set<String> getAuthorities(String resourceId);
}
