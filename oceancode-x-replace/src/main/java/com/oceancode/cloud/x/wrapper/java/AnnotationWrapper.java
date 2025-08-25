package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.Objects;

public class AnnotationWrapper extends BaseJavaClassPartWrapper<AnnotationExpr> {
    public AnnotationWrapper(JavaClassFileWrapper javaClass, AnnotationExpr object, BaseJavaClassPartWrapper<?> parent) {
        super(javaClass, object, parent);
    }

    public boolean isComponentScan() {
        ImportClassWrapper importClassWrapper = file().findImportByClassName(object().getNameAsString());
        if (Objects.nonNull(importClassWrapper)) {
            return "org.springframework.context.annotation.ComponentScan".equals(importClassWrapper.object().getNameAsString());
        }
        return false;
    }

    @Override
    public void replaceAll() {
        object().findAll(ClassOrInterfaceType.class)
                .forEach(it -> {
                    JavaClassFileWrapper javaClassFileWrapper = file().importFile(it.getNameAsString());
                    if (Objects.isNull(javaClassFileWrapper)) {
                        return;
                    }
                    String xName = javaClassFileWrapper.getClassName(false);
                    if (!file().isImported(javaClassFileWrapper)) {
                        xName = javaClassFileWrapper.getFullPackageName(false);
                    }
                    String finalXName = xName;
                    file().addCallback(() -> it.setName(finalXName));
                });
    }
}
