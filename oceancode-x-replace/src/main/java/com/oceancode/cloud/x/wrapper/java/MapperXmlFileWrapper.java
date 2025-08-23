package com.oceancode.cloud.x.wrapper.java;

import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.FileWrapper;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;
import com.oceancode.cloud.x.wrapper.ProjectWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MapperXmlFileWrapper extends FileWrapper {
    private String datasourceId;
    private List<MapperXmlFileWrapper> xmlList;
    private String code;
    private String packageName;
    private MapperClassWrapper mapperSource;

    public MapperXmlFileWrapper(File file, ProjectWrapper projectWrapper, FileWrapper parent) {
        super(file, projectWrapper, parent);
    }

    public String datasourceId() {
        if (Objects.nonNull(datasourceId)) {
            return datasourceId;
        }
        if (file.isFile()) {
            return mapper().datasourceId(false);
        }
        File cur = file;
        datasourceId = "";
        while (true) {
            if (!cur.getAbsolutePath().startsWith(project().getAbsolutePath())) {
                break;
            }
            if (cur.getParentFile().getName().equals(project().getMapperXmlRootDir())) {
                datasourceId = cur.getName();
                return datasourceId;
            }
            if (hasParent()) {
                datasourceId = ((MapperXmlFileWrapper) parent()).datasourceId();
                return datasourceId;
            }
            cur = cur.getParentFile();
        }
        return datasourceId;
    }

    public List<MapperXmlFileWrapper> xmlList() {
        if (Objects.nonNull(xmlList)) {
            return xmlList;
        }
        if (file.isFile()) {
            return Collections.emptyList();
        }

        File[] files = file.listFiles();
        if (Objects.isNull(files)) {
            xmlList = Collections.emptyList();
            return xmlList;
        }

        xmlList = new ArrayList<>();
        for (File f : files) {
            xmlList.add(new MapperXmlFileWrapper(f, project(), this));
        }

        return xmlList;
    }

    @Override
    protected void doReplaceAll() {
        if (!file.isFile()) {
            return;
        }

        code = getReplaceCode().replace(getFullPackageName(true), mapper().getFullPackageName(false));
        for (MethodWrapper method : mapper().methods()) {
            String rawName = method.name(true);
            String xName = method.name(false);
            String rawId = " id=\"" + rawName + "\" ";
            String xId = " id=\"" + xName + "\" ";
            code = code.replace(rawId, xId);
        }
    }

    public MapperClassWrapper mapper() {
        if (Objects.nonNull(mapperSource)) {
            return mapperSource;
        }
        String name = getFullPackageName(true);
        JavaClassFileWrapper target = project().findByPackageName(name);
        if (target instanceof MapperClassWrapper) {
            mapperSource = (MapperClassWrapper) target;
        }
        return mapperSource;
    }

    @Override
    public String name() {
        if (file.isFile()) {
            String name = file.getName();
            name = name.substring(0, name.indexOf("."));
            return name;
        }
        return file.getName();
    }

    @Override
    public String getClassName(boolean isRaw) {
        String name = file.getName();
        name = name.substring(0, name.indexOf("."));
        return isRaw ? name : XUtil.getContext().replaceClassName(getPackageName(true), name);
    }

    @Override
    protected String getSuffix() {
        return ".xml";
    }

    @Override
    public List<MapperXmlFileWrapper> files() {
        if (file.isFile()) {
            return Collections.emptyList();
        }
        File[] files = file.listFiles();
        if (Objects.isNull(files)) {
            return Collections.emptyList();
        }

        return Arrays.stream(files).map(it -> new MapperXmlFileWrapper(it, project(), this))
                .toList();
    }

    @Override
    public String getAbsolutePath(boolean isRaw) {
        String id = mapper().datasourceId(true);
        return project().getOutputResourceDir() + File.separator + project().getMapperXmlRootDir() + File.separator + id + File.separator + getPackageName(isRaw) + File.separator + getClassName(isRaw) + getSuffix();
    }

    @Override
    protected String getReplaceCode() {
        if (Objects.nonNull(code)) {
            return code;
        }
        try {
            code = Files.readString(Path.of(file.getAbsolutePath()));
            String str = code.toLowerCase();
            packageName = "";
            int mapperIndex = str.indexOf("<mapper ");
            if (mapperIndex >= 0) {
                str = code.substring(mapperIndex).trim();
            }
            if (str.contains(">")) {
                str = str.substring(0, str.indexOf(">"));
            }
            if (str.contains("\"")) {
                str = str.substring(str.indexOf("\"") + 1).trim();
            }
            if (str.contains("\"")) {
                str = str.substring(0, str.lastIndexOf("\"")).trim();
            }
            packageName = str.substring(0, str.lastIndexOf("."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return code;
    }

    @Override
    public String getPackageName(boolean isRaw) {
        if (Objects.isNull(packageName)) {
            getReplaceCode();
        }
        if (isRaw) {
            return packageName;
        }
        return XUtil.getContext().replacePackageName(packageName);
    }

    @Override
    protected boolean canReplaced() {
        return true;
    }

    public boolean isMapperXml() {
        return file.exists() && file.isFile();
    }

    public void setMapperSource(MapperClassWrapper mapperSource) {
        this.mapperSource = mapperSource;
    }
}
