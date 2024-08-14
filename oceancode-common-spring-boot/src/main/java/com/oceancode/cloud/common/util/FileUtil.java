/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.entity.PartFile;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.apache.commons.io.FileUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * <B>FileUtil</B>
 *
 * <p>
 * This class is a file util.
 * </p>
 *
 * @author Dynamic Gen
 * @since 1.0
 */
public final class FileUtil {
    private FileUtil() {
    }

    public static PartFile getPartFile(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty() || file.getSize() <= 0) {
            return null;
        }

        PartFile partFile = new PartFile();
        partFile.setFilename(file.getName());
        partFile.setOriginalFilename(file.getOriginalFilename());

        if (ValueUtil.isNotEmpty(file.getOriginalFilename())) {
            if (file.getOriginalFilename().contains(".")) {
                partFile.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            }
        }
        partFile.setSize(file.getSize());
        try {
            partFile.setInputStream(file.getInputStream());
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "get file failed", e);
        }

        partFile.setContentType(file.getContentType());
        return partFile;
    }

    /**
     * read upload file content
     *
     * @param partFile upload file
     * @return file content
     */
    public static String readToString(PartFile partFile) {
        try (InputStreamReader streamReader = new InputStreamReader(partFile.getInputStream());
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    /**
     * read file content
     *
     * @param filePath  filePath
     * @param typeClass return type
     * @param <T>       return type
     * @return target type class
     */
    public static <T> T readerResourceToBean(String filePath, Class<T> typeClass) {
        String content = readerResourceToString(filePath);
        return ValueUtil.isNotEmpty(content) ? JsonUtil.toBean(content, typeClass) : null;
    }

    /**
     * read classpath resource
     *
     * @param filePath classpath resource
     * @return resource content
     */
    public static String readerResourceToString(String filePath) {
        return readerResourceToString(FileUtil.class, filePath);
    }

    /**
     * read classpath resource
     *
     * @param clazz    target classpath class
     * @param filePath resource
     * @return resource content
     */
    public static String readerResourceToString(Class<?> clazz, String filePath) {
        InputStream fileIn = clazz.getClassLoader().getResourceAsStream(filePath);
        if (null == fileIn) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "file not found.");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn, StandardCharsets.UTF_8))) {
            String lineStr;
            StringBuilder sb = new StringBuilder();
            while ((lineStr = reader.readLine()) != null) {
                sb.append(lineStr).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }


    public static void writeStringToFile(File file, String content) {
        try {
            FileUtils.writeStringToFile(file, content, Charset.forName(StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}