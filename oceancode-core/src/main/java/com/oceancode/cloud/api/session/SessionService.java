/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.session;

/**
 * @author qinjiawang
 */
public interface SessionService  {
    /**
     * check user whether login
     *
     * @return true user is login else false
     */
    boolean isLogin();

    /**
     * get login user data info
     *
     * @return user info
     */
    <T>T getUserInfo();

    /**
     * set user session info
     *
     * @param userInfo user info
     */
    <T>void setUserInfo(UserInfo<T> userInfo);

    /**
     * logout
     */
    void logout();
}
