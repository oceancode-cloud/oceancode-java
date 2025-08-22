package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.MethodDeclaration;
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
    protected void doReplaceAll() {
        if (!XUtil.hasAnnotation(object().getAnnotations(), "Override")) {
            String name = XUtil.getContext().replaceMethodName(file().getFullPackageName(true), object().getNameAsString());
            file().addCallback(() -> object().setName(name));
        }

        object().findAll(NameExpr.class).forEach(name -> {
            VariableWrapper variable = file().globalVariable(name.getNameAsString());
            if (Objects.nonNull(variable)) {
                String xName = variable.name(false);
                file().addCallback(() -> name.setName(xName));
            }
        });

        parameters().forEach(ParameterWrapper::replaceAll);
    }

    public List<ParameterWrapper> parameters() {
        return object().getParameters().stream().map(it -> new ParameterWrapper(file(), object().getBody().orElse(null), it)).toList();
    }
}
