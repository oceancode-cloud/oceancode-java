/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.session;

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
    boolean isLogin(String token);

    /**
     * get login user data info
     *
     * @return user info
     */
    UserBaseInfo getUserInfo(String token);

    /**
     * set user session info
     *
     * @param userInfo user info
     */
    void setUserInfo(String token, UserBaseInfo userInfo);

    /**
     * logout
     */
    void logout(String token);
}
