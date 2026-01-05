package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.security.Rsa2Crypto;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.util.Base64;
import java.util.Objects;

public class CompressUtil {
    private CompressUtil() {
    }

    public static Rsa2CryptoService getRsa2CryptoService() {
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class, false);
        if (Objects.isNull(rsa2CryptoService)) {
            return new Rsa2Crypto();
        }
        return rsa2CryptoService;
    }

    public static void unCompress(String filePath, String outDir) {
        unCompress(filePath, null, outDir);
    }

    public static void unCompress(String filePath, String key, String outDir) {
        unCompress(filePath, key, true, outDir);
    }

    public static void unCompress(String filePath, String key, boolean isPrivateKey, String outDir) {
        File file = new File(filePath);
        ZipFile zipFile = new ZipFile(file);
        UnzipParameters parameters = new UnzipParameters();
        try {
            if (zipFile.isEncrypted()) {
                FileHeader target = null;
                for (FileHeader fileHeader : zipFile.getFileHeaders()) {
                    String fileName = fileHeader.getFileName();
                    if (".public".equals(fileName)) {
                        isPrivateKey = true;
                        target = fileHeader;
                    } else if (".private".equals(fileName)) {
                        target = fileHeader;
                        isPrivateKey = false;
                    }
                    if (Objects.nonNull(target)) {
                        break;
                    }
                }
                if (Objects.nonNull(target)) {
                    byte[] keyBytes = zipFile.getInputStream(target).readAllBytes();
                    String ase = new String(keyBytes);
                    String aesKey = isPrivateKey ? getRsa2CryptoService().decryptByPrivateKey(ase, key) :
                            getRsa2CryptoService().decryptByPublicKey(ase, key);
                    zipFile.setPassword(aesKey.toCharArray());
                }
                zipFile.extractAll(outDir, parameters);
                File keyFile = new File(outDir, isPrivateKey ? ".public" : ".private");
                if (keyFile.exists()) {
                    keyFile.delete();
                }
            } else {
                zipFile.extractAll(outDir, parameters);
            }
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    public static void compressDir(String dir, String outputFile) {
        compressDir(dir, null, outputFile);
    }

    public static void compressDir(String dir, String key, String outputFile) {
        compressDir(dir, key, true, outputFile);
    }

    public static void compressDir(String dir, String key, boolean isPublicKey, String outputFile) {
        String aes = randomAesSecret();
        boolean hasKey = ValueUtil.isNotEmpty(key);
        File keyFile = null;
        if (hasKey) {
            String encryptKey = isPublicKey ? getRsa2CryptoService().encryptByPublicKey(aes, key) :
                    getRsa2CryptoService().encryptByPrivateKey(aes, key);
            keyFile = new File(dir, isPublicKey ? ".public" : ".private");
            FileUtil.writeStringToFile(keyFile, encryptKey);
        }
        File dirFile = new File(dir);

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        if (hasKey) {
            parameters.setEncryptionMethod(EncryptionMethod.AES);
        }
        parameters.setEncryptFiles(hasKey);


        try (ZipFile zipFile = new ZipFile(outputFile)) {
            zipFile.setPassword(aes.toCharArray());
            for (File file : dirFile.listFiles()) {
                if (".git".equals(file.getName()) || ".idea".equals(file.getName())) {
                    continue;
                }
                if (".public".equals(file.getName()) || ".private".equals(file.getName())) {
                    zipFile.addFile(file);
                    continue;
                }
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                } else {
                    zipFile.addFile(file, parameters);
                }
            }
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        } finally {
            if (Objects.nonNull(keyFile) && keyFile.exists()) {
                keyFile.delete();
            }
        }
    }

    public static String randomAesSecret() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return encodedKey;
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}
