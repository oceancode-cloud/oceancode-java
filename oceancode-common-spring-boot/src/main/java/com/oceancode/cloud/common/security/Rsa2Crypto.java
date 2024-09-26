package com.oceancode.cloud.common.security;

import com.oceancode.cloud.api.security.RsaKeyPair;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class Rsa2Crypto implements Rsa2CryptoService {

    @Override
    public String encrypt(String input, String key) {
        return encryptByPublicKey(input, key);
    }

    @Override
    public String decrypt(String input, String key) {
        return decryptByPrivateKey(input, key);
    }

    @Override
    public String encryptByPrivateKey(String input, String key) {
        byte[] buffer = null;
        String result = null;
        try {
            if (input != null && ValueUtil.isNotEmpty(key)) {
                byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
                byte[] priBytes = org.apache.commons.codec.binary.Base64.decodeBase64(key);
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(priBytes);
                KeyFactory rsa = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = rsa.generatePrivate(privateKeySpec);
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, privateKey);
                buffer = cipher.doFinal(bytes);
                result = org.apache.commons.codec.binary.Base64.encodeBase64String(buffer);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
        return result;
    }

    @Override
    public String encryptByPublicKey(String input, String key) {
        byte[] buffer = null;
        String result = null;
        try {
            if (input != null && ValueUtil.isNotEmpty(key)) {
                byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
                byte[] pubBytes = org.apache.commons.codec.binary.Base64.decodeBase64(key);
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubBytes);
                KeyFactory rsa = KeyFactory.getInstance("RSA");
                PublicKey publicKey = rsa.generatePublic(publicKeySpec);
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                buffer = cipher.doFinal(bytes);
                result = org.apache.commons.codec.binary.Base64.encodeBase64String(buffer);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
        return result;
    }

    @Override
    public String decryptByPublicKey(String input, String key) {
        byte[] buffer = null;
        String result = null;
        try {
            byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(input);
            byte[] pubBytes = org.apache.commons.codec.binary.Base64.decodeBase64(key);
            X509EncodedKeySpec pkcs8EncodedKeySpec = new X509EncodedKeySpec(pubBytes);
            KeyFactory rsa = KeyFactory.getInstance("RSA");
            PublicKey publicKey = rsa.generatePublic(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            buffer = cipher.doFinal(bytes);

            result = new String(buffer, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
        return result;
    }

    @Override
    public String decryptByPrivateKey(String input, String key) {
        byte[] buffer = null;
        String result = null;
        try {
            byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(input);
            byte[] priBytes = org.apache.commons.codec.binary.Base64.decodeBase64(key);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(priBytes);
            KeyFactory rsa = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = rsa.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            buffer = cipher.doFinal(bytes);

            result = new String(buffer, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
        return result;
    }

    @Override
    public RsaKeyPair generatorKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair res = generator.generateKeyPair();
            return new RsaKeyPair(org.apache.commons.codec.binary.Base64.encodeBase64String(res.getPublic().getEncoded()),
                    org.apache.commons.codec.binary.Base64.encodeBase64String(res.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    @Override
    public String getPublicKeyFromPem(String publicPemKey) {
        try (StringReader stringReader = new StringReader(publicPemKey)) {
            PEMParser pemParser = new PEMParser(stringReader);
            Object obj = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey publicKey = converter.getPublicKey((SubjectPublicKeyInfo) obj);
            return new String(Base64.encodeBase64Chunked(publicKey.getEncoded()));
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}
