package com.oceancode.cloud.x.wrapper;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaParserAdapter;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.java.FieldWrapper;
import com.oceancode.cloud.x.wrapper.java.MapperClassWrapper;
import com.oceancode.cloud.x.wrapper.java.MapperXmlFileWrapper;

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
        List<FileWrapper> list = Arrays.stream(file.listFiles())
                .filter(f -> !project().isExcludeFile(f))
                .filter(f -> "package-info.java".equals(f.getName().toLowerCase()))
                .map(it -> {
                    FileWrapper file = null;
                    if (XUtil.isJavaSourceFile(it)) {
                        JavaClassFileWrapper temp = new JavaClassFileWrapper(it, projectWrapper, this);
                        file = temp;
                        if (temp.isMapper()) {
                            file = new MapperClassWrapper(it, projectWrapper, this);
                        }
                    } else if (it.getName().toLowerCase().endsWith("mapper.xml")) {
                        file = new MapperXmlFileWrapper(it, projectWrapper, this);
                    } else {
                        file = new FileWrapper(it, projectWrapper, this);
                    }
                    project().addFileIndexer(file);
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
        String path = (file.isFile() ? file.getParentFile() : file).getAbsolutePath();
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
        throw new RuntimeException(file + "not implementation.");
    }

    public String getAbsolutePath(boolean isRaw) {
        if (!file.getAbsolutePath().startsWith(project().getAbsolutePath() + project().getSourceCodePath())) {
            return project().getOutputAbstractPath() + project().getSourceCodePath().substring(project().getAbsolutePath().length());
        }
        String path = projectWrapper.getOutputCodeDir();
        String pkgName = getPackageName(isRaw);
        if (Objects.nonNull(pkgName) && !pkgName.isEmpty()) {
            path += pkgName.replace(".", File.separator) + File.separator;
        }
        return path + getClassName(isRaw) + getSuffix();
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
        doReplaceContent();
        File targetFile = new File(getAbsolutePath(!isReplaced));
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }

        XUtil.getContext().addCodeCallback(() -> doWriteFileContent(getAbsolutePath(!isReplaced)));
    }

    protected void doReplaceContent() {
    }

    protected void doWriteFileContent(String absolutePath) {
        if (!XUtil.isJavaSourceFile(file)) {
            File target = new File(absolutePath);
            if (target.exists()) {
                target.delete();
            }
            XUtil.copyFile(file.getAbsolutePath(), absolutePath);
            return;
        }
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
        String curPath = fullName.replace(".", File.separator);
        if (!file.getAbsolutePath().endsWith(curPath)) {
            int len = (project().getAbsolutePath() + project().getSourceCodePath()).length() + curPath.length();
            if (file.getAbsoluteFile().length() > len) {
                return null;
            }
        }
        if (file.isFile()) {
            return null;
        }
        for (FileWrapper f : files()) {
            project().addFileIndexer(f);
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

    public File getFile() {
        return file;
    }


}
