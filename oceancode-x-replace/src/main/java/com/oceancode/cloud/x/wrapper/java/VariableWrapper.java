package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class VariableWrapper extends BaseJavaClassPartWrapper<VariableDeclarator> {
    public VariableWrapper(JavaClassFileWrapper javaClass, VariableDeclarator object, BaseJavaClassPartWrapper<?> parent) {
        super(javaClass, object, parent);
    }

    @Override
    public boolean canReplaced() {
        if (parent instanceof FieldWrapper fieldWrapper) {
            if (fieldWrapper.isMapperField()) {
                return true;
            }
        }
        if (!object().getParentNode().isPresent()) {
            return super.canReplaced();
        }
        Node node = object().getParentNode().get();
        if (node instanceof FieldDeclaration fieldDeclaration) {
            if (!XUtil.canReplaced(fieldDeclaration.getAnnotations())) {
                return false;
            }
        }
        return super.canReplaced();
    }

    @Override
    protected void doReplaceAll() {
        String methodScope = parent instanceof MethodWrapper ? ((MethodWrapper) parent).getParameterScope() : null;
        String rawName = object().getNameAsString();
        String name = XUtil.getContext().replaceVariable(methodScope, getScope(), rawName);
        String finalName = name;
        file().addCallback(() -> object().setName(finalName));

        if (parent instanceof MethodWrapper methodWrapper) {
            replaceMethodCaller(methodWrapper, rawName, name);
            return;
        }

        file().getParse().findAll(FieldAccessExpr.class)
                .stream().filter(field -> field.getNameAsString().equals(rawName))
                .forEach(field -> {
                    file().addCallback(() -> field.setName(name));
                });
    }

    private void replaceMethodCaller(MethodWrapper methodWrapper, String rawName, String name) {
        List<NameExpr> list = methodWrapper.object().findAll(NameExpr.class);
        for (NameExpr nameExpr : list) {
            if (nameExpr.getNameAsString().equals(rawName)) {
                file().addCallback(() -> nameExpr.setName(name));

                JavaClassFileWrapper javaClassFileWrapper = file().importFile(object().getTypeAsString());
                if (Objects.nonNull(javaClassFileWrapper)) {
                    List<MethodCallExpr> methods = methodWrapper.object().findAll(MethodCallExpr.class);
                    for (MethodCallExpr method : methods) {
                        if (!method.getScope().isPresent()) {
                            return;
                        }
                        if (nameExpr.getNameAsString().equals(method.getScope().get().toString())) {
                            MethodWrapper targetMethod = javaClassFileWrapper.method(method.getNameAsString());
                            if (Objects.nonNull(targetMethod)) {
                                String xName = targetMethod.name(false);
                                file().addCallback(() -> method.setName(xName));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String name(boolean isRaw) {
        if (isRaw || Objects.isNull(parent) || !canReplaced()) {
            return super.name(true);
        }
        if (parent instanceof FieldWrapper field) {
            if (field.object().isStatic()) {
                if (field.object().isPrivate()) {
                    return super.name(false);
                }
                return file().getClassName(false) + "." + super.name(false);
            } else {
                return "this." + super.name(false);
            }
        }
        return super.name(isRaw);
    }
}
