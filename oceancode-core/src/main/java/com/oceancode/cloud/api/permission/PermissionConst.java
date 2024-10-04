package com.oceancode.cloud.api.permission;

public class PermissionConst {
    public static final String OPERATION_OR = "or";
    public static final String OPERATION_AND = "and";
    public static final String AUTHORITY_LOGIN = "login";
    public static final String PRIVATE_TOKEN = "PRIVATE-TOKEN";
    public static final String AUTHORITY_UN_LOGIN = "unlogin";

    /**
     * 用户相关权限
     */
    // 示例账号
    public static final String AUTHORITY_USER_EXAMPLE = "user.example";


    /**
     * 资源类型
     */
    public static final int RESOURCE_TYPE_ANY = -1;
    public static final int RESOURCE_TYPE_ADD = 0;
    public static final int RESOURCE_TYPE_ADD_MANY = 1;
    public static final int RESOURCE_TYPE_DELETE = 5;
    public static final int RESOURCE_TYPE_DELETE_MANY = 6;
    public static final int RESOURCE_TYPE_UPDATE = 10;
    public static final int RESOURCE_TYPE_UPDATE_MANY = 11;
    public static final int RESOURCE_TYPE_QUERY = 15;
    public static final int RESOURCE_DOWNLOAD = 20;

    // 安装用户的配置权限判定
    public static final String AUTHORITY_CUSTOM_USER = "custom.user";

    public static final String AUTHORITY_VISITOR = AUTHORITY_UN_LOGIN;
    public static final String AUTHORITY_CRUD_ADD = "add";
    public static final String AUTHORITY_CRUD_ADD_BATCH = "add.batch";
    public static final String AUTHORITY_CRUD_DELETE = "delete";
    public static final String AUTHORITY_CRUD_DELETE_BATCH = "delete.batch";
    public static final String AUTHORITY_CRUD_UPDATE = "update";
    public static final String AUTHORITY_CRUD_UPDATE_BATCH = "update.batch";
    public static final String AUTHORITY_CRUD_QUERY = "list";

}
