package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.List;
import java.util.Objects;

public class ConstructorWrapper extends BaseJavaClassPartWrapper<ConstructorDeclaration> {
    public ConstructorWrapper(JavaClassFileWrapper javaClass, ConstructorDeclaration object) {
        super(javaClass, object);
    }

    @Override
    protected void doReplaceAll() {
        String name = file().getClassName();
        file().addCallback(() -> object().setName(name));

        List<NameExpr> list = object().getBody().findAll(NameExpr.class);
        for (NameExpr nameExpr : list) {
            VariableWrapper variable = file().globalVariable(nameExpr.getNameAsString());
            if (Objects.nonNull(variable)) {
                String xName = variable.name(false);
                file().addCallback(() -> nameExpr.setName(xName));
            }
        }

        parameters().forEach(ParameterWrapper::replaceAll);
    }

    public List<ParameterWrapper> parameters() {
        return object().getParameters().stream().map(it -> new ParameterWrapper(file(), object().getBody(), it)).toList();
    }
}
