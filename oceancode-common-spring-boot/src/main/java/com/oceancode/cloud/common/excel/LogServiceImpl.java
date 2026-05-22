package com.oceancode.cloud.common.excel;

import com.oceancode.cloud.api.file.ExportFileContext;
import com.oceancode.cloud.api.file.FileContext;
import com.oceancode.cloud.api.file.FileInfo;
import com.oceancode.cloud.api.file.FileService;
import com.oceancode.cloud.api.file.ParseCallback;
import com.oceancode.cloud.api.file.ParseFileContext;
import com.oceancode.cloud.api.file.TemplateInputStream;
import com.oceancode.cloud.api.file.WriteCallback;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class LogServiceImpl implements FileService {
//    @Override
    public <T extends ParseFileContext> void parse(T context, ParseCallback callback) {
        File file = new File(context.getFilePath());
        FileContext fileContext = new FileContext();
        try {
            processFile(file, fileContext, callback);
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

//    @Override
    public <T extends ExportFileContext> void write(T context, WriteCallback callback) {

    }

//    @Override
    public void readTemplate(File templateFile, Consumer<TemplateInputStream> consumer) {

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
    public void save(FileInfo fileInfo) {

    }
}
