package com.oceancode.cloud.x.wrapper.java;

import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.FileWrapper;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;
import com.oceancode.cloud.x.wrapper.ProjectWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapperClassWrapper extends JavaClassFileWrapper {
    private List<MapperXmlFileWrapper> xmlFiles;

    public MapperClassWrapper(File file, ProjectWrapper projectWrapper, FileWrapper parent) {
        super(file, projectWrapper, parent);
    }

    @Override
    protected void doReplaceAll() {
        super.doReplaceAll();
        mapperXmlFiles().forEach(MapperXmlFileWrapper::replaceAll);
    }

    @Override
    public String getClassName(boolean isRaw) {
        String name = file.getName();
        name = name.substring(0, name.indexOf("."));
        return isRaw ? name : XUtil.getContext().replaceClassName(getPackageName(true), name);
    }

    public List<MapperXmlFileWrapper> mapperXmlFiles() {
        if (Objects.nonNull(xmlFiles)) {
            return xmlFiles;
        }
        xmlFiles = new ArrayList<>();
        processXmlMapper(xmlFiles, project().mapperXmlFiles());
        return xmlFiles;
    }

    private void processXmlMapper(List<MapperXmlFileWrapper> xmlFiles, List<MapperXmlFileWrapper> mapperXmlFileWrappers) {
        for (MapperXmlFileWrapper mapperXmlFileWrapper : mapperXmlFileWrappers) {
            if (mapperXmlFileWrapper.isMapperXml() && mapperXmlFileWrapper.getFullPackageName(true).equals(getFullPackageName(true))) {
                mapperXmlFileWrapper.setMapperSource(this);
                xmlFiles.add(mapperXmlFileWrapper);
            } else {
                processXmlMapper(xmlFiles, mapperXmlFileWrapper.files());
            }
        }
    }

    @Override
    public String getPackageName(boolean isRaw) {
        if (isRaw) {
            return super.getPackageName(true);
        }
        String name = super.getPackageName(true);
        name = name.substring(name.lastIndexOf(".mapper.") + ".mapper.".length());
        String datasourceId = name.substring(0, name.indexOf("."));
        datasourceId = XUtil.getContext().replaceDatasourceId(datasourceId);
        name = super.getPackageName(false) + "." + datasourceId;
        return name;
    }
}
