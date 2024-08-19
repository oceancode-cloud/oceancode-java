package com.oceancode.cloud.test.data;

import com.oceancode.cloud.common.util.FileUtil;
import com.oceancode.cloud.common.util.JsonUtil;

import java.util.List;
import java.util.Objects;


public class DataProvider {
    private DataProviderType type;
    private String uri;

    private volatile List<Data> datasets;

    private DataProvider(DataProviderType type, String uri) {
        this.type = type;
        this.uri = uri;
    }

    public static DataProvider fromJsonFile(String path) {
        return new DataProvider(DataProviderType.JSON_FILE, path);
    }

    public List<Data> load() {
        if (Objects.isNull(datasets)) {
            synchronized (this) {
                if (Objects.isNull(datasets)) {
                    loadFromFile();
                }
            }
        }
        return datasets;
    }

    private void loadFromFile() {
        datasets = JsonUtil.toList(FileUtil.readerResourceToString(uri), Data.class);
    }
}
