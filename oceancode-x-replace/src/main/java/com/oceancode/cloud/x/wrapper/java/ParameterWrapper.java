package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ParameterWrapper extends BaseJavaClassPartWrapper<Parameter> {
    private BlockStmt body;

    public ParameterWrapper(JavaClassFileWrapper javaClass, BlockStmt body, Parameter object) {
        super(javaClass, object);
        this.body = body;
    }

    public ParameterWrapper(JavaClassFileWrapper javaClass, BlockStmt body, Parameter object, BaseJavaClassPartWrapper<?> parent) {
        super(javaClass, object, parent);
        this.body = body;
    }

    @Override
    protected void doReplaceAll() {
        object().findAll(SimpleName.class)
                .forEach(it -> {
                    if (it.getParentNode().isPresent()) {
                        if (it.getParentNode().get() instanceof Parameter parameter) {
                            String xName = XUtil.getContext().replaceVariable(getScope(), it.getIdentifier());
                            file().addCallback(() -> it.setIdentifier(xName));
                        }
                    }
                });

        Optional<Node> parentNode = Objects.nonNull(body) ? body.getParentNode() : Optional.empty();
        ConstructorDeclaration constructorDeclaration = null;
        MethodDeclaration methodDeclaration = null;
        if (parentNode.isPresent()) {
            Node node = parentNode.get();
            if (node instanceof ConstructorDeclaration con) {
                constructorDeclaration = con;
            } else if (node instanceof MethodDeclaration method) {
                methodDeclaration = method;
            }
        }

        if (Objects.nonNull(body)) {
            Set<String> globalFieldScopes = new HashSet<>();
            if (!file().files().isEmpty()) {
                globalFieldScopes.add(file().fields().get(0).getScope());
            }
            for (NameExpr nameExpr : body.findAll(NameExpr.class)) {
                String rawName = nameExpr.getNameAsString();
                String xName = XUtil.getContext().replaceVariable(globalFieldScopes, getScope(), rawName);
                if (rawName.equals(name(true))) {
                    file().addCallback(() -> nameExpr.setName(xName));
                }
                if (Objects.nonNull(constructorDeclaration) && constructorDeclaration.getParameterByName(rawName).isPresent()) {
                    Parameter parameter = constructorDeclaration.getParameterByName(rawName).get();
                    file().addCallback(() -> parameter.setName(xName));
                }

                if (Objects.nonNull(methodDeclaration) && methodDeclaration.getParameterByName(rawName).isPresent()) {
                    Parameter parameter = methodDeclaration.getParameterByName(rawName).get();
                    file().addCallback(() -> parameter.setName(xName));
                }
            }
        }

        if (file() instanceof MapperClassWrapper mapperClass) {
            String rawName = object().getNameAsString();
            String xName = XUtil.getContext().replaceVariable(getScope(), rawName);
            file().addCallback(() -> object().setName(xName));
        }

    }

    @Override
    protected String getScope() {
        String name = "";
        Node parentNode = null;
        if (Objects.nonNull(body)) {
            if (!body.getParentNode().isPresent()) {
                return super.getScope();
            }
            parentNode = body.getParentNode().get();
        } else if (object().getParentNode().isPresent()) {
            parentNode = object().getParentNode().get();
        }

        if (parentNode instanceof ConstructorDeclaration constructorDeclaration) {
            name = ".ConstructorDeclaration:" + constructorDeclaration.getParameters().toString();
        } else if (parentNode instanceof MethodDeclaration methodDeclaration) {
            name = ".method:" + methodDeclaration.getNameAsString() + "." + methodDeclaration.getParameters().toString();
        }
        return super.getScope() + name;
    }
}
