package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

public class MainClassWrapper extends BaseJavaClassPartWrapper<ClassOrInterfaceDeclaration> {
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
        return true;
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


}
