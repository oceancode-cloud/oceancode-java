package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.PackageDeclaration;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.Objects;

public class PackageWrapper extends BaseJavaClassPartWrapper<PackageDeclaration> {

    public PackageWrapper(JavaClassFileWrapper javaClass, PackageDeclaration object) {
        super(javaClass, object);
    }

    @Override
    protected void doReplaceAll() {
        String name = file().getPackageName();
        if (Objects.isNull(name) || name.isEmpty()) {
            object().remove();
            return;
        }
        file().addCallback(() -> object().setName(name));
    }
}
