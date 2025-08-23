package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainClassWrapper extends BaseJavaClassPartWrapper<ClassOrInterfaceDeclaration> {
    private List<JavaClassFileWrapper> parents;

    public MainClassWrapper(JavaClassFileWrapper javaClass, ClassOrInterfaceDeclaration object) {
        super(javaClass, object);
    }


    @Override
    public boolean canReplaced() {
        if (object().isInterface()) {
            if (file().isMapper()) {
                return true;
            }
            return false;
        }

        return !file().hasPublicStaticMethod();
    }

    @Override
    protected void doReplaceAll() {
        String name = file().getClassName();
        file().addCallback(() -> object().setName(name));

        String rawName = file().getClassName(true);
        file().getParse().findAll(ClassOrInterfaceType.class)
                .stream().filter(it -> it.getNameAsString().equals(rawName))
                .forEach(it -> {
                    file().addCallback(() -> it.setName(name));
                });
    }

    public List<JavaClassFileWrapper> parents() {
        if (Objects.nonNull(parents)) {
            return parents;
        }
        parents = new ArrayList<>();
        object().getImplementedTypes()
                .forEach(classOrInterfaceType -> {
                    String name = classOrInterfaceType.getNameAsString();
                    ImportClassWrapper importClass = file().findImportByClassName(name);
                    if (Objects.nonNull(importClass)) {
                        JavaClassFileWrapper file = file().project().findByPackageName(importClass.object().getNameAsString());
                        if (Objects.nonNull(file)) {
                            parents.add(file);
                        }
                    }
                });
        return parents;
    }

    public boolean isUtil() {
        boolean ret = !object().isInterface() && object().isPublic();
        ret = ret && !file().constructors().isEmpty();
        ret = ret && !file().constructors().stream().anyMatch(constructorWrapper -> constructorWrapper.object().isPublic());
        return ret;
    }

}
