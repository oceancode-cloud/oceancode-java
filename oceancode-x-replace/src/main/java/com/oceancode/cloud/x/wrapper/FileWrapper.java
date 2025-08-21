package com.oceancode.cloud.x.wrapper;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.oceancode.cloud.x.util.XUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class FileWrapper {
    private File file;
    private String rawPackageName;
    private CompilationUnit staticCompilationUnit;

    private ProjectWrapper projectWrapper;

    public FileWrapper(File file, ProjectWrapper projectWrapper) {
        this.file = file;
        this.projectWrapper = projectWrapper;
    }

    public List<FileWrapper> files() {
        List<FileWrapper> list = Arrays.stream(file.listFiles()).map(it -> new FileWrapper(it, projectWrapper))
                .toList();
        return list;
    }


    public boolean isJavaSource() {
        return file.exists() && file.getName().toLowerCase().endsWith(".java");
    }

    private CompilationUnit getParse() {
        if (Objects.nonNull(staticCompilationUnit)) {
            return staticCompilationUnit;
        }
        try {
            staticCompilationUnit = StaticJavaParser.parse(file);
            return staticCompilationUnit;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void replace() {
        replacePackages();
        replaceImplClass();
    }

    private void replaceImplClass() {
        doReplace(getParse().findAll(ClassOrInterfaceDeclaration.class), data -> {
            if (data.isInterface()) {
                return;
            }
            String name = XUtil.getContext().replaceClassName(rawPackageName, data.getNameAsString());
            data.setName(name);
        });
    }

    private <T> void doReplace(List<T> list, Consumer<T> consumer) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            consumer.accept(next);
        }
    }

    public void replacePackages() {
        doReplace(getParse().findAll(PackageDeclaration.class), data -> {
                    data.setName(XUtil.getContext().replacePackageName(data.getNameAsString()))
                }

        );
    }

    public String getRawPackageName() {
        if (Objects.nonNull(rawPackageName)) {
            return rawPackageName;
        }
        String path = file.getAbsolutePath();
        String srcDir = "src" + File.separator + "main" + File.separator + "java";
        path = path.substring(projectWrapper.getFile().getAbsolutePath().length());
        path = path.substring(srcDir.length());
        rawPackageName = path.replace(File.separator, ".").trim();
        if (rawPackageName.startsWith(".")) {
            rawPackageName = rawPackageName.substring(1);
        }
        if (rawPackageName.endsWith(".")) {
            rawPackageName = rawPackageName.substring(0, rawPackageName.lastIndexOf("."));
        }
        return rawPackageName;
    }
}
