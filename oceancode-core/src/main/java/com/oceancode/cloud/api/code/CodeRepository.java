package com.oceancode.cloud.api.code;

import java.util.List;
import java.util.function.Supplier;

public interface CodeRepository {
    CodeRepository username(String username);

    CodeRepository password(String password);

    CodeRepository password(Supplier<String> passwordConsumer);

    CodeRepository id(String id);

    String getId();

    void close();

    CodeRepository addFiles(List<CodeFile> files);

    CodeRepository addFile(CodeFile file);

    CodeRepository deleteFiles(List<String> paths);

    CodeRepository deleteFile(String path);

    String getCodePath();

    String getRepositoryPath();

    void clone(String branch);

    String getBranch();

    Object getClient();

    void commit(String message);

    void pull();

    void push(String branch);
}
