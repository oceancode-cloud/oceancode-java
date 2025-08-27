package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ReturnStmt;
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
        parameters().forEach(ParameterWrapper::replaceAll);

        object().findAll(NameExpr.class).forEach(name -> {
            VariableWrapper variable = file().globalVariable(name.getNameAsString());
            if (Objects.nonNull(variable)) {
                String xName = variable.name(false);
                file().addCallback(() -> name.setName(xName));
            }
        });

        object().findAll(MethodCallExpr.class)
                .forEach(methodCallExpr -> {
                    Expression scope = null;
                    if (methodCallExpr.getScope().isPresent()) {
                        scope = methodCallExpr.getScope().get();
                        replaceScope(scope);
                    }
                    MethodWrapper method = file().method(methodCallExpr.getNameAsString());
                    String xName = null;
                    if (Objects.nonNull(method)) {
                        xName = method.name(false);
                    } else {
                        if (Objects.nonNull(scope)) {
                            ParameterWrapper parameter = parameter(scope.toString());
                            if (Objects.nonNull(parameter)) {
                                xName = getPojoName(parameter.object().getTypeAsString(), methodCallExpr.getNameAsString());
                            }
                        }
                    }
                    if (Objects.nonNull(xName)) {
                        String finalXName = xName;
                        file().addCallback(() -> methodCallExpr.setName(finalXName));
                    }
                });
        replaceMethodClassType();
        object().findAll(NameExpr.class)
                .forEach(nameExpr -> {
                    String xName = getNameScope(nameExpr);
                    if (Objects.isNull(xName)) {
                        VariableWrapper variable = file().globalVariable(nameExpr.getNameAsString());
                        if (Objects.nonNull(variable)) {
                            xName = variable.name(false);
                        }
                    }
                    if (Objects.nonNull(xName)) {
                        String finalXName = xName;
                        file().addCallback(() -> nameExpr.setName(finalXName));
                    }
                });
        if (!object().getBody().isPresent()) {
            return;
        }

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
                    JavaClassFileWrapper javaClassFileWrapper = file().importFile(name);
                    if (Objects.isNull(javaClassFileWrapper)) {
                        return;
                    }
                    if (!javaClassFileWrapper.canReplaced()) {
                        if (javaClassFileWrapper.getPackageName().equals(file().getPackageName())) {
                            return;
                        }
                    }
                    String xName = javaClassFileWrapper.getClassName(false);
                    if (!file().isImported(javaClassFileWrapper)) {
                        xName = javaClassFileWrapper.getFullPackageName(false);
                    }
                    String finalXName = xName;
                    file().addCallback(() -> classOrInterfaceType.setName(finalXName));
                });

        replaceVariables();

    }

    private String getNameScope(NameExpr nameExpr) {
        if (!nameExpr.getParentNode().isPresent()) {
            return null;
        }
        ParameterWrapper parameter = parameter(nameExpr.getNameAsString());
        if (Objects.nonNull(parameter)) {
            Node node = nameExpr.getParentNode().get();
            if (node instanceof ObjectCreationExpr objectCreationExpr) {
                if (!objectCreationExpr.getScope().isPresent()) {
                    ClassOrInterfaceType classOrInterfaceType = objectCreationExpr.findFirst(ClassOrInterfaceType.class).orElse(null);
                    if (Objects.nonNull(classOrInterfaceType)) {
                        return parameter.name(false);
                    }
                }
            } else if (node instanceof AssignExpr assignExpr) {
                return parameter.name(false);
            }
        }
        return null;
    }

    public boolean isGetter() {
        if (file().mainClass().isInterface()) {
            return false;
        }
        boolean ret = object().isPublic() && !object().isStatic();
        if (!ret) {
            return false;
        }
        ReturnStmt returnStmt = object().getBody().get().findFirst(ReturnStmt.class).orElse(null);
        if (Objects.isNull(returnStmt)) {
            return false;
        }

        if (!returnStmt.getExpression().isPresent()) {
            return false;
        }
        String fieldName = returnStmt.getExpression().get().toString();
        if (fieldName.startsWith("this.")) {
            fieldName = fieldName.substring("this.".length());
        }
        FieldWrapper field = file().field(fieldName);
        if (Objects.isNull(field)) {
            return false;
        }
        if (object().getTypeAsString().equals(field.getElementType())) {
            return true;
        }
        return false;
    }

    public boolean isSetter() {
        if (file().mainClass().isInterface()) {
            return false;
        }
        boolean ret = object().isPublic() && !object().isStatic() && object().getBody().isPresent();
        if (!ret) {
            return false;
        }
        ReturnStmt returnStmt = object().getBody().get().findFirst(ReturnStmt.class).orElse(null);
        if (Objects.nonNull(returnStmt)) {
            return false;
        }
        if (parameters().size() != 1) {
            return false;
        }

        List<FieldAccessExpr> list = object().getBody().get().findAll(FieldAccessExpr.class);

        if (list.size() != 1) {
            return false;
        }

        FieldAccessExpr fieldAccessExpr = list.get(0);
        ParameterWrapper parameterWrapper = parameters().get(0);
        if (!fieldAccessExpr.getParentNode().isPresent()) {
            return false;
        }
        String valueName = null;
        if (fieldAccessExpr.getParentNode().get() instanceof AssignExpr assignExpr) {
            valueName = assignExpr.getValue().toString();
        }
        if (Objects.isNull(valueName)) {
            return false;
        }
        if (!parameterWrapper.name(true).equals(valueName)) {
            return false;
        }
        FieldWrapper field = file().field(fieldAccessExpr.getNameAsString());
        if (Objects.isNull(field)) {
            return false;
        }
        if (!parameterWrapper.object().getTypeAsString().equals(field.getElementType())) {
            return false;
        }
        return true;
    }

    private String getPojoName(String className, String name) {
        JavaClassFileWrapper javaClassFileWrapper = file().importFile(className);
        if (Objects.isNull(javaClassFileWrapper)) {
            return null;
        }
        String fieldName = null;
        boolean isSetter = false;
        if (name.startsWith("get")) {
            if (!javaClassFileWrapper.mainClass().hasGetter()) {
                return null;
            }
            fieldName = name.substring("get".length());
        } else if (name.startsWith("set")) {
            if (!javaClassFileWrapper.mainClass().hasSetter()) {
                return null;
            }
            fieldName = name.substring("set".length());
            isSetter = true;
        }
        if (Objects.isNull(fieldName)) {
            return null;
        }
        fieldName = XUtil.lowerMethod(fieldName);
        FieldWrapper field = javaClassFileWrapper.field(fieldName);
        if (Objects.nonNull(field)) {
            String var = field.name(false);
            if (var.startsWith("this.")) {
                var = var.substring("this.".length());
            }
            var = (isSetter ? "set" : "get") + XUtil.upperMethod(var);
            return var;
        }
        return null;
    }


    private void replaceMethodClassType() {
        object().findAll(ClassOrInterfaceType.class)
                .forEach(it -> {
                    JavaClassFileWrapper javaClassFileWrapper = file().importFile(it.getNameAsString());
                    if (Objects.isNull(javaClassFileWrapper) || !javaClassFileWrapper.canReplaced()) {
                        return;
                    }
                    String xName = javaClassFileWrapper.getClassName(false);
                    if (!file().isImported(javaClassFileWrapper)) {
                        xName = javaClassFileWrapper.getFullPackageName(false);
                    }
                    String finalXName = xName;
                    file().addCallback(() -> it.setName(finalXName));
                });
    }

    private void replaceVariables() {
        List<VariableWrapper> list = object().getBody().get().findAll(VariableDeclarator.class)
                .stream().map(it -> new VariableWrapper(file(), it, this)).toList();
        for (VariableWrapper variableWrapper : list) {
            variableWrapper.replaceAll();
        }
    }

    public String getParameterScope() {
        if (parameters().isEmpty()) {
            return null;
        }
        return parameters().get(0).getScope();
    }

    private void replaceScope(Expression expression) {
        List<SimpleName> list = expression.findAll(SimpleName.class);
        for (SimpleName simpleName : list) {
            String rawName = simpleName.getIdentifier();
            JavaClassFileWrapper javaClassFileWrapper = file().importFile(rawName);
            if (Objects.isNull(javaClassFileWrapper) || !javaClassFileWrapper.canReplaced()) {
                return;
            }
            String xName = javaClassFileWrapper.getClassName(false);
            if (!file().isImported(javaClassFileWrapper)) {
                xName = javaClassFileWrapper.getFullPackageName(false);
            }
            String finalXName = xName;
            file().addCallback(() -> simpleName.setIdentifier(finalXName));
        }

    }

    @Override
    protected void doReplaceAll() {
        String xMethodName = name(false);
        file().addCallback(() -> object().setName(xMethodName));
    }


    public List<ParameterWrapper> parameters() {
        return object().getParameters().stream().map(it -> new ParameterWrapper(file(), object().getBody().orElse(null), it, this)).toList();
    }

    public ParameterWrapper parameter(String name) {
        return parameters().stream().filter(it -> it.name(true).equals(name))
                .findFirst().orElse(null);
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
        boolean isGetter = false;
        boolean isSetter = false;
        boolean ret = true;
        if (isGetter()) {
            isGetter = true;
            ret = false;
        } else if (isSetter()) {
            isSetter = true;
            ret = false;
        }
        if (ret && file().mainClass().object().isInterface()) {
            return super.name(true);
        }
        String name = XUtil.getContext().replaceMethodName(getScope(), object().getNameAsString());
        if (isGetter) {
            return "get" + XUtil.upperMethod(name);
        } else if (isSetter) {
            return "set" + XUtil.upperMethod(name);
        }
        return name;
    }

    public boolean isMain() {
        boolean ret = object().isPublic() && object().isStatic();
        ret = ret && "main".equals(object().getNameAsString());
        return ret;
    }
}
