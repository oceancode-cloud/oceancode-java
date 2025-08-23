package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.List;
import java.util.Objects;

public class MethodWrapper extends BaseJavaClassPartWrapper<MethodDeclaration> {
    public MethodWrapper(JavaClassFileWrapper javaClass, MethodDeclaration object) {
        super(javaClass, object);
    }

    @Override
    public boolean canReplaced() {
        if (object().isStatic() && object().isPublic()) {
            return false;
        }
        boolean canReplaced = XUtil.canReplaced(object().getAnnotations());
        if (!canReplaced) {

            return false;
        }
//        if (!object().isPrivate()) {
//            List<JavaClassFileWrapper> parents = file().mainClass().parents();
//            for (JavaClassFileWrapper item : parents) {
//                if (item.hasMethod(object().getNameAsString())) {
//                    return false;
//                }
//            }
//        }
        return canReplaced;
    }

    @Override
    protected void doRenderContent() {
        if (!object().getBody().isPresent()) {
            return;
        }
        object().findAll(NameExpr.class).forEach(name -> {
            VariableWrapper variable = file().globalVariable(name.getNameAsString());
            if (Objects.nonNull(variable)) {
                String xName = variable.name(false);
                file().addCallback(() -> name.setName(xName));
            }
        });

        object().findAll(MethodCallExpr.class)
                .forEach(methodCallExpr -> {
                    if (methodCallExpr.getScope().isPresent()) {
                        return;
                    }
                    MethodWrapper method = file().method(methodCallExpr.getNameAsString());
                    if (Objects.nonNull(method)) {
                        String xName = method.name(false);
                        file().addCallback(() -> methodCallExpr.setName(xName));
                    }
                });
        object().getBody().get().findAll(SimpleName.class)
                .forEach(simpleName -> {
                    if (!simpleName.getParentNode().isPresent()) {
                        return;
                    }
                    if (!(simpleName.getParentNode().get() instanceof MethodCallExpr method)) {
                        return;
                    }
                    if (!method.getScope().isPresent()) {
                        return;
                    }
                    String field = method.getScope().get().toString();
                    FieldWrapper fieldWrapper = file().field(field);
                    if (Objects.isNull(fieldWrapper)) {
                        if (simpleName.getParentNode().get().toString().startsWith("mapper.")) {
                            List<FieldWrapper> list = file().fields().stream().filter(it -> it.isMapperField()).toList();
                            if (list.size() == 1) {
                                fieldWrapper = list.get(0);
                            }
                        }
                    }
                    if (Objects.isNull(fieldWrapper) || !fieldWrapper.isMapperField()) {
                        return;
                    }
                    String xName;
                    MethodWrapper targetMethod = fieldWrapper.typeFile().method(simpleName.getIdentifier());
                    if (Objects.nonNull(targetMethod)) {
                        xName = fieldWrapper.typeFile().method(simpleName.getIdentifier()).name(false);
                    } else {
                        xName = simpleName.getIdentifier();
                    }
                    file().addCallback(() -> simpleName.setIdentifier(xName));
                });
        object().getBody().get().findAll(ClassOrInterfaceType.class)
                .forEach(classOrInterfaceType -> {
                    String name = classOrInterfaceType.getNameAsString();
                    ImportClassWrapper importClassWrapper = file().findImportByClassName(name);
                    if (Objects.nonNull(importClassWrapper)) {
                        JavaClassFileWrapper target = file().project().findByPackageName(importClassWrapper.object().getNameAsString(), true);
                        if (Objects.nonNull(target)) {
                            String xName = target.getClassName();
                            file().addCallback(() -> classOrInterfaceType.setName(xName));
                        }
                    }
                });
        parameters().forEach(ParameterWrapper::replaceAll);
    }

    @Override
    protected void doReplaceAll() {
        String xMethodName = name(false);
        file().addCallback(() -> object().setName(xMethodName));
    }

    public List<ParameterWrapper> parameters() {
        return object().getParameters().stream().map(it -> new ParameterWrapper(file(), object().getBody().orElse(null), it)).toList();
    }

    @Override
    protected String getScope() {
        return file().getFullPackageName(true);
    }

    @Override
    public String name(boolean isRaw) {
        if (isRaw) {
            return super.name(isRaw);
        }
        return XUtil.getContext().replaceMethodName(getScope(), object().getNameAsString());
    }
}
