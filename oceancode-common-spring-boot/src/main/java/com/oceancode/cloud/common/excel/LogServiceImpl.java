package com.oceancode.cloud.common.excel;

import com.oceancode.cloud.api.excel.FileContext;
import com.oceancode.cloud.api.excel.FileService;
import com.oceancode.cloud.api.excel.ParseCallback;
import com.oceancode.cloud.api.excel.WritCallback;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class LogServiceImpl implements FileService {
    @Override
    public void parse(String path, InputStream inputStream, ParseCallback callback) {
        File file = new File(path);
        FileContext fileContext = new FileContext();
        try {
            processFile(file, fileContext, callback);
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    private void processFile(File file, FileContext fileContext, ParseCallback callback) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.isNull(files)) {
                return;
            }
            for (File it : files) {
                processFile(it, fileContext, callback);
            }
            return;
        }

        BiggerFileReader reader = new BiggerFileReader(file, 65536);
        byte[] leftBytes = null;
        while (reader.read() != -1) {
            byte[] array = reader.getArray();
            byte[] contents = array;
            if (Objects.nonNull(leftBytes)) {
                contents = new byte[leftBytes.length + array.length];
                System.arraycopy(leftBytes, 0, contents, 0, leftBytes.length);
                System.arraycopy(array, 0, contents, leftBytes.length, array.length);
            }
            ByteRow byteRow = new ByteRow(contents);
            callback.parse(fileContext, byteRow);
            leftBytes = byteRow.getLeftBytes();
        }
    }

    @Override
    public void parse(String path, ParseCallback callback) {
        parse(path, null, callback);
    }

    @Override
    public void write(String templatePath, OutputStream outputStream, WritCallback callback) {

    }

    @Override
    public void write(String templatePath, WritCallback callback) {

    }
}
