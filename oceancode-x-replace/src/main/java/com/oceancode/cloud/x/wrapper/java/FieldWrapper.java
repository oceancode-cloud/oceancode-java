package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
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

    public boolean isMapperField() {
        JavaClassFileWrapper target = typeFile();
        return Objects.nonNull(target) && target.isMapper();
    }

    public JavaClassFileWrapper typeFile() {
        List<ClassOrInterfaceType> list = object().findAll(ClassOrInterfaceType.class);
        if (list.size() != 1) {
            return null;
        }
        ImportClassWrapper importClassWrapper = file().findImportByClassName(list.get(0).getNameAsString());
        if (Objects.isNull(importClassWrapper)) {
            return null;
        }
        JavaClassFileWrapper target = file().project().findByPackageName(importClassWrapper.object().getNameAsString());
        return target;
    }

    @Override
    public String name(boolean isRaw) {
        if (variables().size() != 1) {
            return super.name(isRaw);
        }
        VariableWrapper name = variables().get(0);
        return name.name(isRaw);
    }
}
