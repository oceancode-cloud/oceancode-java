package com.oceancode.cloud.x.wrapper;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParserAdapter;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.java.MapperClassWrapper;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileWrapper {
    protected File file;
    private String rawPackageName;
    private CompilationUnit staticCompilationUnit;
    private String packageName;

    private ProjectWrapper projectWrapper;
    private FileWrapper parent;

    public FileWrapper(File file, ProjectWrapper projectWrapper, FileWrapper parent) {
        this.file = file;
        this.projectWrapper = projectWrapper;
        this.parent = parent;
    }

    public <T extends FileWrapper> List<T> files() {
        List<FileWrapper> list = Arrays.stream(file.listFiles()).map(it -> {
                    FileWrapper file = null;
                    if (XUtil.isJavaSourceFile(it)) {
                        JavaClassFileWrapper temp = new JavaClassFileWrapper(it, projectWrapper, this);
                        file = temp;
                        if (temp.isMapper()) {
                            file = new MapperClassWrapper(it, projectWrapper, this);
                        }
                    } else {
                        file = new FileWrapper(it, projectWrapper, this);
                    }
                    return file;
                })
                .toList();
        return (List<T>) list;
    }

    public CompilationUnit getParse() {
        if (Objects.nonNull(staticCompilationUnit)) {
            return staticCompilationUnit;
        }
        try {
            ParserConfiguration configuration = new ParserConfiguration();
            configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.BLEEDING_EDGE);
            staticCompilationUnit = new JavaParserAdapter(new JavaParser(configuration)).parse(file);
            return staticCompilationUnit;
        } catch (Exception e) {
            System.err.println(file.getAbsoluteFile());
            e.printStackTrace();
        }
        return null;
    }

    protected String getReplaceCode() {
        CompilationUnit parse = getParse();
        return Objects.nonNull(parse) ? parse.toString() : "";
    }

    protected boolean hasParent() {
        return Objects.nonNull(parent);
    }

    protected FileWrapper parent() {
        return parent;
    }


    public String getPackageName() {
        return getPackageName(false);
    }

    public String getPackageName(boolean isRaw) {
        if (Objects.nonNull(packageName)) {
            return isRaw ? packageName : XUtil.getContext().replacePackageName(packageName);
        }
        String path = file.getParentFile().getAbsolutePath();
        path = path.substring(projectWrapper.getAbsolutePath().length());
        if (path.length() >= projectWrapper.getSourceCodePath().length()) {
            path = path.substring(projectWrapper.getSourceCodePath().length());
        }

        path = path.replace(File.separator, ".").trim();
        if (path.startsWith(".")) {
            path = path.substring(1);
        }
        packageName = path;
        return isRaw ? packageName : XUtil.getContext().replacePackageName(packageName);
    }

    public String getClassName() {
        return getClassName(false);
    }

    public String getClassName(boolean isRaw) {
        throw new RuntimeException("not implementation.");
    }

    public String getAbsolutePath(boolean isRaw) {
        return projectWrapper.getOutputCodeDir() + File.separator + getPackageName(isRaw).replace(".", File.separator) + File.separator + getClassName(isRaw) + getSuffix();
    }

    protected String getSuffix() {
        String suffix = file.getName();
        suffix = suffix.substring(suffix.lastIndexOf("."));
        return suffix;
    }


    public void replaceAll() {
        if (file.isDirectory()) {
            for (FileWrapper f : files()) {
                f.replaceAll();
            }
            return;
        }
        boolean isReplaced = canReplaced();
        if (isReplaced) {
            init();
            doReplaceAll();
        }

        File targetFile = new File(getAbsolutePath(!isReplaced));
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        XUtil.getContext().addCodeCallback(() -> doWriteFileContent(getAbsolutePath(!isReplaced)));
    }

    protected void doWriteFileContent(String absolutePath) {
        XUtil.writeFile(absolutePath, getReplaceCode());
    }

    protected void init() {
        getPackageName(true);
    }

    public void addCallback(Runnable runnable) {
        XUtil.getContext().addCallback(runnable);
    }

    public ProjectWrapper project() {
        return projectWrapper;
    }

    protected void doReplaceAll() {

    }

    protected boolean canReplaced() {
        return false;
    }

    public JavaClassFileWrapper findByPackageName(String fullName) {
        if (file.isFile()) {
            return null;
        }
        for (FileWrapper f : files()) {
            JavaClassFileWrapper targetFile = f.findByPackageName(fullName);
            if (Objects.nonNull(targetFile)) {
                return targetFile;
            }
        }
        return null;
    }

    public String getFullPackageName(boolean isRaw) {
        return getPackageName(isRaw) + "." + getClassName(isRaw);
    }

    public boolean isSourceFile() {
        return false;
    }

    public String name() {
        return file.getName();
    }
}
