package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.Objects;

public class ImportClassWrapper extends BaseJavaClassPartWrapper<ImportDeclaration> {
    public ImportClassWrapper(JavaClassFileWrapper javaClass, ImportDeclaration object) {
        super(javaClass, object);
    }

    public JavaClassFileWrapper importFile() {
        return file().project().findByPackageName(object().getNameAsString(), true);
    }

    @Override
    public boolean canReplaced() {
        return Objects.nonNull(importFile());
    }

    @Override
    protected void doReplaceAll() {
        if (importFile().isMapper()) {
            file().getParse().findAll(ClassOrInterfaceType.class)
                    .stream().forEach(classOrInterfaceType -> {
                        String name = classOrInterfaceType.getNameAsString();
                        ImportClassWrapper importClassWrapper = file().findImportByClassName(name);
                        if (Objects.nonNull(importClassWrapper) && classOrInterfaceType.getNameAsString().equals(importFile().getClassName(true))) {
                            String xName = importFile().getClassName(false);
                            file().addCallback(() -> classOrInterfaceType.setName(xName));
                        }
                    });

        }
        String xName = importFile().getFullPackageName(false);
        file().addCallback(() -> object().setName(xName));

    }
}
