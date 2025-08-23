package com.oceancode.cloud.x.wrapper;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.java.ConstructorWrapper;
import com.oceancode.cloud.x.wrapper.java.FieldWrapper;
import com.oceancode.cloud.x.wrapper.java.ImportClassWrapper;
import com.oceancode.cloud.x.wrapper.java.MainClassWrapper;
import com.oceancode.cloud.x.wrapper.java.MethodWrapper;
import com.oceancode.cloud.x.wrapper.java.PackageWrapper;
import com.oceancode.cloud.x.wrapper.java.VariableWrapper;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JavaClassFileWrapper extends FileWrapper {
    public final static JavaClassFileWrapper EMPTY = new JavaClassFileWrapper(null, null, null);
    private PackageWrapper pkg;
    private MainClassWrapper mainClass;
    private List<ImportClassWrapper> imports;
    private List<ConstructorWrapper> constructors;
    private List<FieldWrapper> fields;
    private List<MethodWrapper> methods;

    public JavaClassFileWrapper(File file, ProjectWrapper projectWrapper, FileWrapper parent) {
        super(file, projectWrapper, parent);
    }

    @Override
    public List<FileWrapper> files() {
        return Collections.emptyList();
    }

    @Override
    public String getClassName(boolean isRaw) {
        String name = file.getName();
        name = name.substring(0, name.indexOf("."));
        return isRaw ? name : XUtil.getContext().replaceClassName(getPackageName(true), name);
    }

    @Override
    protected String getSuffix() {
        return ".java";
    }

    @Override
    protected boolean canReplaced() {
        return mainClass().canReplaced();
    }

    @Override
    protected void doReplaceAll() {
        pkg().replaceAll();
        imports().stream().forEach(ImportClassWrapper::replaceAll);
        mainClass().replaceAll();
        fields().forEach(FieldWrapper::replaceAll);
        constructors().forEach(ConstructorWrapper::replaceAll);
        methods().forEach(MethodWrapper::replaceAll);
    }

    public List<FieldWrapper> fields() {
        if (Objects.nonNull(fields)) {
            return fields;
        }
        fields = getParse().findAll(FieldDeclaration.class)
                .stream().map(it -> new FieldWrapper(this, it))
                .toList();
        return fields;
    }

    public List<VariableWrapper> globalVariables() {
        return fields().stream().flatMap(it -> it.variables().stream()).toList();
    }

    public VariableWrapper globalVariable(String name) {
        return globalVariables().stream().filter(it -> name.equals(it.name(true)))
                .findFirst().orElse(null);
    }

    public MainClassWrapper mainClass() {
        if (Objects.nonNull(mainClass)) {
            return mainClass;
        }
        ClassOrInterfaceDeclaration declaration = getParse().findAll(ClassOrInterfaceDeclaration.class)
                .stream().filter(it -> it.getNameAsString().equals(getClassName(true)))
                .findFirst().orElse(null);
        mainClass = new MainClassWrapper(this, declaration);
        return mainClass;
    }

    private PackageWrapper pkg() {
        PackageDeclaration declaration = getParse().findAll(PackageDeclaration.class)
                .stream().filter(pkg -> Objects.equals(pkg.getNameAsString(), getPackageName(true)))
                .findFirst().orElse(null);
        pkg = new PackageWrapper(this, declaration);
        return pkg;
    }


    public List<ImportClassWrapper> imports() {
        if (Objects.nonNull(imports)) {
            return imports;
        }

        imports = getParse().findAll(ImportDeclaration.class)
                .stream().map(it -> new ImportClassWrapper(this, it))
                .toList();

        return imports;
    }

    public ImportClassWrapper findImportByClassName(String className) {
        return imports().stream().filter(importClassWrapper -> importClassWrapper.object().getNameAsString().endsWith("." + className))
                .findFirst().orElse(null);
    }

    @Override
    public JavaClassFileWrapper findByPackageName(String fullName) {
        if (Objects.equals(getFullPackageName(true), fullName)) {
            return this;
        }
        return null;
    }

    public List<ConstructorWrapper> constructors() {
        if (Objects.nonNull(constructors)) {
            return constructors;
        }
        constructors = getParse().findAll(ConstructorDeclaration.class)
                .stream().map(it -> new ConstructorWrapper(this, it))
                .toList();

        return constructors;
    }

    public List<MethodWrapper> methods() {
        if (Objects.nonNull(methods)) {
            return methods;
        }
        methods = getParse().findAll(MethodDeclaration.class).stream()
                .map(it -> new MethodWrapper(this, it))
                .toList();
        return methods;
    }

    public MethodWrapper method(String rawName) {
        return methods().stream().filter(method -> method.name(true).equals(rawName))
                .filter(method -> method.canReplaced())
                .findAny().orElse(null);
    }

    public boolean hasMethod(String rawName) {
        return Objects.nonNull(method(rawName));
    }

    public boolean hasPublicStaticMethod() {
        return methods().stream().anyMatch(methodWrapper -> methodWrapper.object().isPublic() && methodWrapper.object().isStatic());
    }

    @Override
    public boolean isSourceFile() {
        return true;
    }

    public boolean isMapper() {
        MainClassWrapper mainClassWrapper = mainClass();
        if (Objects.isNull(mainClassWrapper)) {
            return false;
        }
        return mainClassWrapper.object().isInterface() && XUtil.hasAnnotation(mainClass().object().getAnnotations(), "Mapper");
    }
}
