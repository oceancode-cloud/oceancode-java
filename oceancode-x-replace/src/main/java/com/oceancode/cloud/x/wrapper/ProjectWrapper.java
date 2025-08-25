package com.oceancode.cloud.x.wrapper;

import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.java.MapperXmlFileWrapper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectWrapper {
    private File projectDir;
    private File replaceDir;
    private String sourceCodeDir;
    private Set<String> basePackages = new HashSet<>();
    private Map<String, JavaClassFileWrapper> packageFileMapping = new ConcurrentHashMap<>();
    private Map<String, JavaClassFileWrapper> fileIndexerMapping = new ConcurrentHashMap<>();
    private List<MapperXmlFileWrapper> mapperXmlFiles;
    private FileWrapper packageRootFile;
    private FileWrapper rootPackageFile;
    private Set<String> excludePaths = new HashSet<>();

    public ProjectWrapper(String projectDir, String replaceDir) {
        this(projectDir, null, replaceDir);
    }

    public ProjectWrapper(String projectDir, String modelDir, String replaceDir) {
        if (Objects.nonNull(modelDir)) {
            this.projectDir = new File(projectDir, modelDir);
        } else {
            this.projectDir = new File(projectDir);
        }
        this.replaceDir = new File(replaceDir, getProjectName());
    }


    public FileWrapper getPackageFile() {
        if (Objects.nonNull(packageRootFile)) {
            return packageRootFile;
        }
        File file = new File(projectDir, getSourceCodePath());
        if (file.exists()) {
            packageRootFile = new FileWrapper(file.getParentFile(), this, null);
            return packageRootFile;
        }
        packageRootFile = findRootPackageFile(file);
        return packageRootFile;
    }

    private FileWrapper findRootPackageFile(File dir) {
        if (!dir.exists()) {
            return null;
        }

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (Objects.isNull(files) || files.length == 0) {
                return null;
            }
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".java")) {
                    return new FileWrapper(file.getParentFile(), this, null);
                }
            }
            for (File file : files) {
                FileWrapper rootPackageFile = findRootPackageFile(file);
                if (Objects.nonNull(rootPackageFile)) {
                    return rootPackageFile;
                }
            }
        }
        return null;
    }

    public String getProjectName() {
        return projectDir.getName();
    }

    public List<FileWrapper> files() {
        if (basePackages.isEmpty()) {
            return getPackageFile().files();
        }
        List<FileWrapper> list = new ArrayList<>();
        for (String basePackage : basePackages) {
            File file = new File(projectDir, getSourceCodePath() + basePackage.replace(".", "/"));
            if (!file.exists()) {
                continue;
            }
            if (file.isFile()) {
                continue;
            }
            list.add(new FileWrapper(file, this, null));
        }
        return list;
    }

    public void replaceAll() {
        List<FileWrapper> files = files();
        if (files.isEmpty()) {
            return;
        }
        if (!replaceDir.exists()) {
            replaceDir.mkdir();
        }

        File codeDir = new File(replaceDir, getSourceCodePath());
        if (!codeDir.exists()) {
            codeDir.mkdirs();
        }
        File pomFile = new File(projectDir, "pom.xml");
        File targetPomFile = new File(replaceDir, pomFile.getName());
        if (pomFile.exists() && !targetPomFile.exists()) {
            XUtil.copyFile(pomFile.getAbsolutePath(), targetPomFile.getAbsolutePath());
        }
        for (FileWrapper file : files) {
            file.replaceAll();
        }
    }

    public String getAbsolutePath() {
        return projectDir.getAbsolutePath() + File.separator;
    }

    public String getSourceCodePath() {
        sourceCodeDir = "src" + File.separator + "main" + File.separator + "java" + File.separator;
        return sourceCodeDir;
    }

    public String getOutputCodeDir() {
        return Path.of(replaceDir.getAbsolutePath(), getSourceCodePath()).toString() + File.separator;
    }

    public String getOutputAbstractPath() {
        return replaceDir.getAbsolutePath() + File.separator;
    }

    public String getOutputResourceDir() {
        return Path.of(replaceDir.getAbsolutePath(), getResourceRootDir()).toString();
    }

    public JavaClassFileWrapper findByPackageName(String fullName) {
        return findByPackageName(fullName, null);
    }

    public JavaClassFileWrapper findByPackageName(String fullName, Boolean canReplaced) {
        JavaClassFileWrapper javaClassFile = getJavaClassFile(fullName);
        if (Objects.nonNull(javaClassFile)) {
            return javaClassFile;
        }
        JavaClassFileWrapper javaClassFileWrapper = packageFileMapping.computeIfAbsent(fullName, key -> {
            List<FileWrapper> files = getPackageFile().files();
            for (FileWrapper file : files) {
                JavaClassFileWrapper target = file.findByPackageName(fullName);
                if (Objects.nonNull(target)) {
                    return target;
                }
            }
            JavaClassFileWrapper target = getJavaClassFile(fullName);
            if (Objects.nonNull(target)) {
                return target;
            }
            return JavaClassFileWrapper.EMPTY;
        });
        if (javaClassFileWrapper == JavaClassFileWrapper.EMPTY) {
            return null;
        }
        if (Objects.isNull(canReplaced)) {
            return javaClassFileWrapper;
        } else if (canReplaced) {
            if (!javaClassFileWrapper.canReplaced()) {
                return null;
            }
        } else {
            if (javaClassFileWrapper.canReplaced()) {
                return null;
            }
        }
        return javaClassFileWrapper;
    }

    public void addBasePackages(String basePackage) {
        basePackages.add(basePackage);
    }

    public String getResourceRootDir() {
        return "src" + File.separator + "main" + File.separator + getResourceDir() + File.separator;
    }

    public String getResourceDir() {
        return "resources";
    }

    public String getMapperXmlRootDir() {
        return "repository";
    }

    public List<MapperXmlFileWrapper> mapperXmlFiles() {
        if (Objects.nonNull(mapperXmlFiles)) {
            return mapperXmlFiles;
        }
        File mapperFile = new File(projectDir, getResourceRootDir() + getMapperXmlRootDir());
        if (!mapperFile.exists()) {
            mapperXmlFiles = Collections.emptyList();
            return mapperXmlFiles;
        }
        mapperXmlFiles = new ArrayList<>();
        mapperXmlFiles.add(new MapperXmlFileWrapper(mapperFile, this, null));
        return mapperXmlFiles;
    }

    public void addExcludePath(String path) {
        File file = new File(projectDir, path);
        if (!file.exists()) {
            throw new RuntimeException(file.getAbsolutePath() + " not exists.");
        }
        this.excludePaths.add(file.getAbsolutePath());
    }

    public boolean isExcludeFile(File file) {
        if (excludePaths.contains(file.getAbsolutePath())) {
            return true;
        }
        return excludePaths.stream().anyMatch(path -> file.getAbsolutePath().startsWith(path));
    }

    public void addFileIndexer(FileWrapper fileWrapper) {
        if (fileWrapper instanceof JavaClassFileWrapper javaClass) {
            fileIndexerMapping.put(javaClass.getFullPackageName(true), javaClass);
        }
        if (Objects.nonNull(rootPackageFile)) {
            return;
        }
        if (fileWrapper.getFile().getAbsolutePath().startsWith(getAbsolutePath() + getSourceCodePath())) {
            if (fileWrapper.getFile().isDirectory()) {
                File[] files = fileWrapper.getFile().listFiles();
                if (Objects.nonNull(files)) {
                    boolean hasFile = Arrays.stream(files).anyMatch(file -> file.isFile());
                    if (hasFile) {
                        rootPackageFile = fileWrapper;
                    }
                }
            }
        }
    }

    public JavaClassFileWrapper getJavaClassFile(String fullPackageName) {
        return fileIndexerMapping.get(fullPackageName);
    }

    public FileWrapper getRootPackageFile() {
        return rootPackageFile;
    }
}
