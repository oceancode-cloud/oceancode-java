package com.oceancode.cloud.common.security;

import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AesCrypto implements AesCryptoService {
    @Override
    public String encrypt(String input, String key) {
        if (ValueUtil.isEmpty(input)) {
            return input;
        }
        if (ValueUtil.isEmpty(key)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key must not be empty.");
        }
        try {
            String[] keys = key.split(":");
            IvParameterSpec iv = new IvParameterSpec(keys[0].getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(keys[1].getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(input.getBytes());
            return org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, ex);
        }
    }

    @Override
    public String decrypt(String input, String key) {
        if (ValueUtil.isEmpty(input)) {
            return input;
        }
        if (ValueUtil.isEmpty(key)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key must not be empty.");
        }
        try {
            String[] keys = key.split(":");
            IvParameterSpec iv = new IvParameterSpec(keys[0].getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(keys[1].getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(input));

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, ex);
        }
    }
}
