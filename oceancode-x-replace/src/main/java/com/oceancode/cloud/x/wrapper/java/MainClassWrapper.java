package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPublicModifier;
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
        if (Objects.isNull(object())) {
            if (isAnnotation()) {
                return false;
            }
            return !isEnum();
        }
        if (object().isInterface()) {
            if (file().isMapper()) {
                return true;
            }
            return false;
        }

        if (file().hasMain()) {
            return true;
        }

        return !file().hasPublicStaticMethod();
    }

    private boolean isEnum() {
        if (Objects.nonNull(object())) {
            return false;
        }
        return file().getParse().findAll(EnumDeclaration.class)
                .stream().anyMatch(it -> it.isPublic() && it.getNameAsExpression().equals(file().getClassName(true)));
    }

    private boolean isAnnotation() {
        if (Objects.nonNull(object())) {
            return false;
        }
        return file().getParse().findAll(AnnotationDeclaration.class)
                .stream().anyMatch(NodeWithPublicModifier::isPublic);
    }

    @Override
    protected void doReplaceAll() {
        if (isAnnotation()) {
            return;
        }
        String name = file().getClassName();
        file().addCallback(() -> object().setName(name));


        String rawName = file().getClassName(true);
        file().getParse().findAll(ClassOrInterfaceType.class)
                .stream().filter(it -> it.getNameAsString().equals(rawName))
                .forEach(it -> {
                    String xName = name;
                    String finalXName = xName;
                    file().addCallback(() -> it.setName(finalXName));
                });
    }

    @Override
    protected void doRenderContent() {
        if (Objects.isNull(object())) {
            return;
        }
        replaceAnnotation();
        replaceImplementation();
        replaceInit();
    }

    private void replaceInit() {
        if (isInterface() || isAnnotation()) {
            return;
        }
        object().findAll(InitializerDeclaration.class)
                .forEach(it -> {
                    if (it.isStatic()) {
                        it.getBody().findAll(NameExpr.class)
                                .forEach(nameExpr -> {
                                    FieldWrapper field = file().field(nameExpr.getNameAsString());
                                    if (Objects.nonNull(field) && field.object().isStatic()) {
                                        String xName = field.name(false);
                                        if (xName.contains(".")) {
                                            xName = xName.substring(xName.indexOf(".") + 1);
                                        }
                                        String finalXName = xName;
                                        file().addCallback(() -> nameExpr.setName(finalXName));
                                    }
                                });
                    }
                });
    }

    private void replaceImplementation() {
        NodeList<ClassOrInterfaceType> implementedTypes = object().getImplementedTypes();
        for (ClassOrInterfaceType implementedType : implementedTypes) {
            JavaClassFileWrapper targetJava = file().importFile(implementedType.getNameAsString());
            if (Objects.nonNull(targetJava)) {
                if (!targetJava.getPackageName(false).equals(file().getPackageName(false))) {
                    String xName = targetJava.getFullPackageName(false);
                    file().addCallback(() -> implementedType.setName(xName));
                }
            }
        }
    }

    private void replaceAnnotation() {
        object().getAnnotations().forEach(annotationExpr -> {
            if ("MapperScan".equals(annotationExpr.getNameAsString())) {
                annotationExpr.findAll(MemberValuePair.class).forEach(memberValuePair -> {
                    if ("basePackages".equals(memberValuePair.getNameAsString())) {
                        memberValuePair.findAll(StringLiteralExpr.class).forEach(stringLiteralExpr -> {
                            String datasourceId = stringLiteralExpr.getValue();
                            File file = new File(file().project().getAbsolutePath() + file().project().getSourceCodePath(),
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
                    } else {
                        replaceOtherAnnotation(annotationExpr);
                    }
                });
            }
        });
    }

    private void replaceOtherAnnotation(AnnotationExpr annotationExpr) {
        AnnotationWrapper annotationWrapper = new AnnotationWrapper(file(), annotationExpr, this);
        annotationWrapper.replaceAll();
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

    public boolean isInterface() {
        return Objects.nonNull(object()) && object().isInterface();
    }

    public boolean hasSetter() {
        if (isInterface()) {
            return false;
        }
        if (isAnnotation()) {
            return false;
        }
        return XUtil.hasAnnotation(object().getAnnotations(), "Data", "Setter");
    }

    public boolean hasGetter() {
        if (isInterface()) {
            return false;
        }
        if (isAnnotation()) {
            return false;
        }
        return XUtil.hasAnnotation(object().getAnnotations(), "Data", "Getter");
    }
}
