package com.oceancode.cloud.common.web.crypto;

import com.oceancode.cloud.api.crypto.CryptoData;
import com.oceancode.cloud.api.crypto.CryptoDataService;
import com.oceancode.cloud.api.crypto.CryptoType;
import com.oceancode.cloud.api.crypto.Decrypt;
import com.oceancode.cloud.api.crypto.DecryptCryptoFunction;
import com.oceancode.cloud.api.crypto.Encrypt;
import com.oceancode.cloud.api.crypto.EncryptCryptoFunction;
import com.oceancode.cloud.api.security.CryptoService;
import com.oceancode.cloud.api.security.KeyManager;
import com.oceancode.cloud.api.security.KeyType;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.security.AesCrypto;
import com.oceancode.cloud.common.util.Base64Util;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Primary
public class CryptoDataServiceImpl implements CryptoDataService {

    @Resource
    private CommonConfig commonConfig;

    @Resource
    private Rsa2CryptoService rsa2CryptoService;

    @Resource
    private AesCrypto aesCrypto;

    @Autowired(required = false)
    private KeyManager keyManager;

    private String createCode() {
        String code = "123456790ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder codes = new StringBuilder();
        int codeLength = 16;
        int length = code.length();
        for (int i = 0; i < codeLength; i++) {
            int pos = ThreadLocalRandom.current().nextInt(1000) % length;
            codes.append(code.charAt(pos));
        }
        return codes.toString();
    }

    @Override
    public <T extends Encrypt> void encode(T data, CryptoType cryptoType) {
        CryptoData cryptoData = data.data();
        if (Objects.isNull(cryptoType)) {
            cryptoType = CryptoType.OTHER;
        }
        if (CryptoType.RSA_AES.equals(cryptoType)) {
            String iv = createCode();
            String aesKey = createCode();
            String publicKey = getPublicKey(cryptoData.getId());

            cryptoData.setKey(rsa2CryptoService.encryptByPublicKey(iv + ":" + aesKey, publicKey));
            EncryptCryptoFunction function = value -> {
                String targetValue = null;
                if (Objects.isNull(value)) {
                    return null;
                }
                if (value instanceof String) {
                    targetValue = value.toString();
                } else {
                    targetValue = String.valueOf(value);
                }
                if (Objects.isNull(targetValue)) {
                    return null;
                }
                return aesCrypto.encrypt(targetValue, iv + ":" + aesKey);
            };
            data.encrypt(function);
        } else if (CryptoType.BASE64.equals(cryptoType)) {
            EncryptCryptoFunction function = value -> Base64Util.encodeObject(value);
            data.encrypt(function);
        } else if (CryptoType.AES.equals(cryptoType)) {
            String key = cryptoData.getKey();
            if (ValueUtil.isEmpty(key)) {
                key = ApiUtil.getSecretKey();
            }
            cryptoData.setType(cryptoType.getValue());
            String privateKey = getPrivateKey(cryptoData.getId());
            String aesKey = rsa2CryptoService.decryptByPrivateKey(key, privateKey);
            EncryptCryptoFunction function = value -> {
                String targetValue = null;
                if (Objects.isNull(value)) {
                    return null;
                }
                if (value instanceof String) {
                    targetValue = value.toString();
                } else {
                    targetValue = String.valueOf(value);
                }
                if (Objects.isNull(targetValue)) {
                    return null;
                }
                return aesCrypto.encrypt(targetValue, aesKey);
            };
            data.encrypt(function);
        } else {
            CryptoService cryptoService = ComponentUtil.getStrategyBean(CryptoService.class, cryptoData.getType());
            EncryptCryptoFunction function = value -> cryptoService.encrypt(value, cryptoData.getKey());
            data.encrypt(function);
        }
    }

    private String getPublicKey(String id) {
        commonConfig.getValue("oc.api.crypto.response.secret");
        String publicKey = null;
        if (Objects.nonNull(keyManager)) {
            String keys = keyManager.getKey(id, KeyType.PUBLIC);
            if (ValueUtil.isNotEmpty(keys)) {
                publicKey = keys;
            }
        }
        return publicKey;
    }

    private String getPrivateKey(String id) {
        String privateKey = commonConfig.getValue("oc.api.crypto.request.secret");
        if (Objects.nonNull(keyManager)) {
            String key = keyManager.getKey(id, KeyType.PRIVATE);
            if (ValueUtil.isNotEmpty(key)) {
                privateKey = key;
            }
        }

        return privateKey;
    }

    @Override
    public <T extends Decrypt> void decode(T data, CryptoType cryptoType) {
        CryptoData cryptoData = data.data();
        if (Objects.isNull(cryptoType)) {
            cryptoType = CryptoType.OTHER;
        }
        if (CryptoType.RSA_AES.equals(cryptoType)) {
            String privateKey = getPrivateKey(cryptoData.getId());
            String key = rsa2CryptoService.decryptByPrivateKey(cryptoData.getKey(), privateKey);
            DecryptCryptoFunction function = value -> {
                String targetValue = null;
                if (Objects.isNull(value)) {
                    return null;
                } else if (value instanceof String) {
                    targetValue = value.toString();
                } else {
                    targetValue = String.valueOf(value);
                }

                if (Objects.isNull(targetValue)) {
                    return null;
                }
                return aesCrypto.decrypt(targetValue, key);
            };
            data.decrypt(function);
        } else if (CryptoType.AES.equals(cryptoType)) {
            String secretKey = cryptoData.getKey();
            if (ValueUtil.isEmpty(secretKey)) {
                secretKey = ApiUtil.getSecretKey();
            }
            String key = rsa2CryptoService.decryptByPrivateKey(secretKey, getPrivateKey(cryptoData.getId()));
            DecryptCryptoFunction function = value -> {
                String targetValue = null;
                if (Objects.isNull(value)) {
                    return null;
                } else if (value instanceof String) {
                    targetValue = value.toString();
                } else {
                    targetValue = String.valueOf(value);
                }

                if (Objects.isNull(targetValue)) {
                    return null;
                }
                return aesCrypto.decrypt(targetValue, key);
            };
            data.decrypt(function);
        } else {
            CryptoService cryptoService = ComponentUtil.getStrategyBean(CryptoService.class, cryptoData.getType());
            DecryptCryptoFunction function = value -> cryptoService.decrypt(value, cryptoData.getKey());
            data.decrypt(function);
        }
    }

    @Override
    public boolean isSupport(CryptoType type) {
        return CryptoType.RSA_AES.equals(type);
    }
}
