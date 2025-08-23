package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.io.File;
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

        replaceAnnotation();

        String rawName = file().getClassName(true);
        file().getParse().findAll(ClassOrInterfaceType.class)
                .stream().filter(it -> it.getNameAsString().equals(rawName))
                .forEach(it -> {
                    String xName = name;
                    String finalXName = xName;
                    file().addCallback(() -> it.setName(finalXName));
                });
    }

    private void replaceAnnotation() {
        object().getAnnotations().forEach(annotationExpr -> {
            if ("MapperScan".equals(annotationExpr.getNameAsString())) {
                annotationExpr.findAll(MemberValuePair.class).forEach(memberValuePair -> {
                    if ("basePackages".equals(memberValuePair.getNameAsString())) {
                        memberValuePair.findAll(StringLiteralExpr.class).forEach(stringLiteralExpr -> {
                            String datasourceId = stringLiteralExpr.getValue();
                            File file = new File(file().project().getAbsolutePath() + File.separator + file().project().getSourceCodePath(),
                                    datasourceId.replace(".", File.separator));
                            if (file.exists() && file.isDirectory()) {
                                File targetFile = XUtil.findFirstFileFromDir(file);
                                if (Objects.nonNull(targetFile)) {
                                    JavaClassFileWrapper target = new MapperClassWrapper(targetFile, file().project(), null);
                                    if (Objects.nonNull(target)) {
                                        String packageName = target.getPackageName(false);
                                        file().addCallback(() -> stringLiteralExpr.setValue(packageName));
                                    }
                                }
                            }
                        });
                    }
                });
            }
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
