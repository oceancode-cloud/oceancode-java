/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.session;

import com.oceancode.cloud.api.MapTransfer;

import java.util.Map;

/**
 * @author qinjiawang
 */
public interface SessionService {
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
    Map<String, Object> getUserInfo();

    /**
     * set user session info
     *
     * @param userInfo user info
     */
    void setUserInfo(UserInfo userInfo);

    /**
     * logout
     */
    void logout();
}
