package com.oceancode.cloud.test.ui.component.impl;

import com.oceancode.cloud.test.ui.component.GroupTree;
import com.oceancode.cloud.test.ui.component.Tree;
import com.oceancode.cloud.test.ui.container.Dropdown;
import com.oceancode.cloud.test.ui.container.UIContainer;

public class VxeTree extends Tree {
    private int level = 0;

    public VxeTree(UIContainer root, UIContainer container) {
        super(root, container);
    }

    public VxeTree(UIContainer root, UIContainer container, Tree parent, int level) {
        super(root, container, parent);
        this.level = level;
    }

    @Override
    public Tree tree(String nodeName) {
        String className = ".row--level-" + this.level;
        return new VxeTree(root(), root().container(className, nodeName), this, level + 1);
    }

    @Override
    public int getDepth() {
        return this.level;
    }

    @Override
    public void expand() {
        if (isExpand()) {
            return;
        }
        container().container(".vxe-icon-square-plus-fill").click();
    }

    @Override
    public void collapse() {
        if (!isExpand()) {
            return;
        }
        container().container(".vxe-icon-square-minus-fill").click();
    }

    @Override
    public boolean isExpand() {
        return container().container(".vxe-icon-square-minus-fill").exists();
    }

    @Override
    public Dropdown menu() {
        container().contextmenu();
        return new Dropdown(root(), root().locator(".vue-contextmenu-listWrapper"));
    }
}
