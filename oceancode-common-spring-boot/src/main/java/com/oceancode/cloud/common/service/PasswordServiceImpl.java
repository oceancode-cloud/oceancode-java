package com.oceancode.cloud.common.service;


import com.oceancode.cloud.api.security.PasswordCryptoService;
import com.oceancode.cloud.common.util.PasswordUtil;

public final class PasswordServiceImpl implements PasswordCryptoService {
    @Override
    public String encode(String input) {
        return PasswordUtil.encode(input);
    }

    @Override
    public boolean matches(String raw, String encoded) {
        return PasswordUtil.matches(raw, encoded);
    }
}
