package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.List;
import java.util.Objects;

public class FieldWrapper extends BaseJavaClassPartWrapper<FieldDeclaration> {
    private List<VariableWrapper> variables;

    public FieldWrapper(JavaClassFileWrapper javaClass, FieldDeclaration object) {
        super(javaClass, object);
    }

    @Override
    protected void doReplaceAll() {
        variables().forEach(VariableWrapper::replaceAll);
    }

    public List<VariableWrapper> variables() {
        if (Objects.nonNull(variables)) {
            return variables;
        }
        variables = object().getVariables().stream().map(it -> new VariableWrapper(file(), it, this)).toList();
        return variables;
    }
}
