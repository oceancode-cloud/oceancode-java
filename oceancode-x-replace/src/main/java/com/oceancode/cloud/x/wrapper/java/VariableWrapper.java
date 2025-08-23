package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

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
        String rawName = object().getNameAsString();
        String name = XUtil.getContext().replaceVariable(getScope(), rawName);
        String finalName = name;
        file().addCallback(() -> object().setName(finalName));

        file().getParse().findAll(FieldAccessExpr.class)
                .stream().filter(field -> field.getNameAsString().equals(rawName))
                .forEach(field -> {
                    file().addCallback(() -> field.setName(name));
                });
    }

    @Override
    public String name(boolean isRaw) {
        if (isRaw || Objects.isNull(parent) || !canReplaced()) {
            return super.name(true);
        }
        if (parent instanceof FieldWrapper field) {
            if (field.object().isStatic()) {
                return file().getClassName(false) + "." + super.name(false);
            } else {
                return "this." + super.name(false);
            }
        }
        return super.name(isRaw);
    }
}
