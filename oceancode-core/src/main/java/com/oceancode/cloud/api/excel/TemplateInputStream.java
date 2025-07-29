package com.oceancode.cloud.api.excel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateInputStream implements AutoCloseable {
    private File file;
    private final static Map<String, InnerInputStream> STREAMS = new ConcurrentHashMap<>();

    public TemplateInputStream(File file) {
        this.file = file;
    }

    private static class InnerInputStream {
        private FileInputStream fileInputStream;
        private BufferedInputStream bufferedInputStream;

        public boolean close() {
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Objects.nonNull(bufferedInputStream)) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
    }

    public InputStream getInputStream() {
        return STREAMS.computeIfAbsent(file.getAbsolutePath(), key -> {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            InnerInputStream innerInputStream = new InnerInputStream();
            innerInputStream.fileInputStream = fileInputStream;
            innerInputStream.bufferedInputStream = new BufferedInputStream(fileInputStream);
            return innerInputStream;
        }).bufferedInputStream;
    }

    @Override
    public void close() throws Exception {
        InnerInputStream inputStream = STREAMS.get(file.getAbsolutePath());
        if (Objects.nonNull(inputStream)) {
            synchronized (inputStream) {
                if (Objects.nonNull(inputStream)) {
                    if (inputStream.close()) {
                        STREAMS.remove(file.getAbsolutePath());
                    }
                }
            }
        }
    }
}
