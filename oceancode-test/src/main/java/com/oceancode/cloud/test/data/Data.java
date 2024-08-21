package com.oceancode.cloud.test.data;

import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.entity.PartFile;
import com.oceancode.cloud.common.entity.StringTypeMap;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Data {
    private String name;
    private String description;
    private Boolean positive;

    private Map<String, Object> datasets;

    private List<Runnable> releaseQueue = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPositive() {
        return positive;
    }

    public void setPositive(Boolean positive) {
        this.positive = positive;
    }

    public Map<String, Object> getDatasets() {
        return datasets;
    }

    public void setDatasets(Map<String, Object> datasets) {
        this.datasets = datasets;
    }

    public boolean isPositive() {
        return Objects.nonNull(positive) && positive;
    }

    public Object get(String key) {
        if (datasets == null) {
            return null;
        }
        return datasets.get(key);
    }

    public String getString(String key, String defaultValue) {
        Object val = get(key);
        return val == null ? defaultValue : val.toString();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Long.parseLong(value + "");
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value + "");
    }

    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public boolean hasData() {
        return Objects.nonNull(datasets) && !datasets.isEmpty();
    }

    public File getFile(String key, File defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof File) {
            return (File) value;
        }
        String path = ((String) value).trim();
        String workspace = ComponentUtil.getBean(CommonConfig.class).getValue("oc.test.workspace.dir");
        if (ValueUtil.isNotEmpty(workspace)) {
            path = workspace + path;
        }
        if (path.startsWith(".")) {
            path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + path;
        }
        if (ValueUtil.isNotEmpty(path)) {
            return new File(path);
        }
        return defaultValue;
    }

    public File getFile(String key) {
        return getFile(key, null);
    }

    public PartFile getPartFile(String key) {
        File file = getFile(key);
        if (Objects.isNull(file)) {
            return null;
        }
        PartFile partFile = new PartFile();
        partFile.setFilename(file.getName());
        partFile.setFilename(file.getName());
        partFile.setSize(file.getTotalSpace());
        partFile.setOriginalFilename(file.getName());
        if (!file.exists()) {
            return partFile;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            partFile.setInputStream(fileInputStream);
            releaseQueue.add(() -> {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (file.getName().contains(".")) {
            partFile.setSuffix(file.getName().substring(file.getName().lastIndexOf(".") + 1));
        }

        return partFile;
    }

    public Map<String, Object> getMap(String key) {
        Object value = get(key);
        if (value == null || !(value instanceof Map)) {
            return Collections.emptyMap();
        }
        return (Map<String, Object>) value;
    }

    public void release() {
        Iterator<Runnable> iterator = releaseQueue.iterator();
        while (iterator.hasNext()) {
            Runnable runnable = iterator.next();
            try {
                runnable.run();
            } catch (Throwable e) {
                //ignore
            } finally {
                iterator.remove();
            }
        }
    }

    public <T> T getMap2Bean(String key, Class<T> typeClass) {
        return JsonUtil.mapToBean(getMap(key), typeClass);
    }

    @Override
    public String toString() {
        return (isPositive() ? "正例" : "反例") + "·" + name;
    }
}
