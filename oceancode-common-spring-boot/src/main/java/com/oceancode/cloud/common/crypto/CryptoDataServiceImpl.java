package com.oceancode.cloud.common.crypto;

import com.oceancode.cloud.api.crypto.CryptoData;
import com.oceancode.cloud.api.crypto.CryptoDataService;
import com.oceancode.cloud.api.crypto.CryptoType;
import com.oceancode.cloud.api.crypto.Decrypt;
import com.oceancode.cloud.api.crypto.Encrypt;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.security.AesCrypto;
import com.oceancode.cloud.common.util.ComponentUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

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
            String publicKey = commonConfig.getValue("oc.api.crypto.response.secret");
            cryptoData.setKey(rsa2CryptoService.encryptByPublicKey(iv + ":" + aesKey, publicKey));
            cryptoData.setData(aesCrypto.encrypt(data.rawData(), iv + ":" + aesKey));
        } else {
            CryptoDataService cryptoDataService = ComponentUtil.getStrategyBean(CryptoDataService.class, cryptoType);
            cryptoDataService.encode(data, cryptoType);
        }
    }

    @Override
    public <T extends Decrypt> void decode(T data, CryptoType cryptoType) {
        CryptoData cryptoData = data.data();
        if (Objects.isNull(cryptoType)) {
            cryptoType = CryptoType.OTHER;
        }
        if (CryptoType.RSA_AES.equals(cryptoType)) {
            String privateKey = commonConfig.getValue("oc.api.crypto.request.secret");
            String key = rsa2CryptoService.decryptByPrivateKey(cryptoData.getKey(), privateKey);
            data.decrypt(aesCrypto.decrypt(cryptoData.getData(), key));
        } else {
            CryptoDataService cryptoDataService = ComponentUtil.getStrategyBean(CryptoDataService.class, cryptoType);
            cryptoDataService.decode(data, cryptoType);
        }
    }

    @Override
    public boolean isSupport(CryptoType type) {
        return CryptoType.RSA_AES.equals(type);
    }
}
