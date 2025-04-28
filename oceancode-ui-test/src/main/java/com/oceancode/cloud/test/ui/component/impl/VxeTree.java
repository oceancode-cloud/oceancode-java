package com.oceancode.cloud.test.ui.component.impl;

import com.microsoft.playwright.Locator;
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
        expandIcon().click();
    }

    private Locator expandIcon() {
        Locator it = container().locator("i");
        if (it.count() == 1) {
            return it;
        }
        return container().locator().locator(".vxe-icon-square-plus-fill");
    }

    @Override
    public void collapse() {
        if (!isExpand()) {
            return;
        }
        expandIcon().click();
    }

    @Override
    public boolean isExpand() {
        return expandIcon().count() > 0;
    }

    @Override
    public Dropdown menu() {
        container().contextmenu();
        return new Dropdown(root(), root().locator(".vue-contextmenu-listWrapper"));
    }
}
