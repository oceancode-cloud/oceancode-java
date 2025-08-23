package com.oceancode.cloud.x.wrapper.java;

import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.oceancode.cloud.x.util.XUtil;
import com.oceancode.cloud.x.wrapper.JavaClassFileWrapper;

import java.util.Objects;

public class BaseJavaClassPartWrapper<T> {
    private JavaClassFileWrapper javaClass;
    private T object;
    private boolean replaced = false;
    protected BaseJavaClassPartWrapper<?> parent;

    public BaseJavaClassPartWrapper(JavaClassFileWrapper javaClass, T object) {
        this.javaClass = javaClass;
        this.object = object;
    }

    public BaseJavaClassPartWrapper(JavaClassFileWrapper javaClass, T object, BaseJavaClassPartWrapper<?> parent) {
        this.javaClass = javaClass;
        this.object = object;
        this.parent = parent;
    }

    public JavaClassFileWrapper file() {
        return this.javaClass;
    }

    public T object() {
        return object;
    }

    protected String getScope() {
        return file().getFullPackageName(true);
    }

    public boolean canReplaced() {
        return Objects.nonNull(object());
    }

    public void replaceAll() {
        if (!canReplaced()) {
            doRenderContent();
            return;
        }
        if (replaced) {
            return;
        }
        doReplaceAll();
        doRenderContent();
        this.replaced = true;
    }

    protected void doRenderContent() {

    }

    protected void doReplaceAll() {
    }

    public String name(boolean isRaw) {
        if (!canReplaced()) {
            isRaw = true;
        }
        if (object() instanceof NodeWithSimpleName node) {
            String name = node.getNameAsString();
            return isRaw ? name :
                    XUtil.getContext().replaceVariable(getScope(), name);
        }
        return null;
    }

}
