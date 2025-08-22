package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.ImportDeclaration;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.Objects;

public class ImportClassWrapper extends BaseJavaClassPartWrapper<ImportDeclaration> {
    public ImportClassWrapper(JavaClassFileWrapper javaClass, ImportDeclaration object) {
        super(javaClass, object);
    }

    public JavaClassFileWrapper importFile() {
        return file().project().findByPackageName(object().getNameAsString());
    }

    @Override
    public boolean canReplaced() {
        return super.canReplaced() && Objects.nonNull(importFile());
    }

    @Override
    protected void doReplaceAll() {

    }
}
