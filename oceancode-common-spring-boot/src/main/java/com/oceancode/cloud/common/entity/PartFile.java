/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.entity;

import java.io.InputStream;

public class PartFile {

    private String filename;

    private InputStream inputStream;

    private Long size;

    private String contentType;

    private String suffix;

    private transient SaveFileCallback callback;

    public PartFile(SaveFileCallback callback) {
        this.callback = callback;
    }

    public interface SaveFileCallback<T> {
        void transferTo(PartFile partFile, T param);
    }

    public <T> void transferTo(T param) {
        callback.transferTo(this, param);
    }

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
}
