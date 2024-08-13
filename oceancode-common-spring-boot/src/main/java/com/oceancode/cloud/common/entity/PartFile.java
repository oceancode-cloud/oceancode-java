/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.enums.FileType;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.io.InputStream;
import java.util.Objects;

public class PartFile {

    private String filename;

    private String originalFilename;

    private InputStream inputStream;

    private Long size;

    private String contentType;

    private String suffix;
    private transient FileType fileType;
    private transient String magicNumber;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @JsonIgnore
    public boolean isImage() {
        return FileType.JPEG.equals(getFileType()) ||
                FileType.JPG.equals(getFileType()) ||
                FileType.PNG.equals(getFileType()) ||
                FileType.GIF.equals(getFileType());

    }

    @JsonIgnore
    public boolean isXml() {
        return FileType.XML.equals(getFileType());
    }

    @JsonIgnore
    public boolean isExcle() {
        return FileType.XLS.equals(getFileType()) ||
                FileType.XLSX.equals(getFileType());
    }

    @JsonIgnore
    public boolean isDoc() {
        return FileType.DOC.equals(getFileType()) ||
                FileType.DOCX.equals(getFileType());
    }

    @JsonIgnore
    public boolean isPdf() {
        return FileType.PDF.equals(getFileType());
    }

    @JsonIgnore
    public FileType getFileType() {
        if (Objects.nonNull(fileType)) {
            return fileType;
        }
        fileType = TypeEnum.from(getMagicNumber(), FileType.class);

        return fileType;
    }

    @JsonIgnore
    public String getMagicNumber() {
        if (Objects.isNull(magicNumber)) {
            return magicNumber;
        }
        InputStream is = getInputStream();
        try {
            byte[] fileHeader = new byte[4];
            is.read(fileHeader);
            magicNumber = byteArray2Hex(fileHeader);
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "get file magic failed");
        }

        return magicNumber;
    }

    private static String byteArray2Hex(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        if (data == null || data.length == 0) {
            return null;
        }
        for (byte datum : data) {
            int v = datum & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        String result = stringBuilder.toString();
        return result;
    }
}
