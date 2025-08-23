package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
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
                    MethodWrapper method = file().method(methodCallExpr.getNameAsString());
                    if (Objects.nonNull(method)) {
                        String xName = method.name(false);
                        file().addCallback(() -> methodCallExpr.setName(xName));
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
