package com.oceancode.cloud.test.ui.component.impl;

import com.microsoft.playwright.Locator;
import com.oceancode.cloud.test.ui.component.GroupTree;
import com.oceancode.cloud.test.ui.component.Tree;
import com.oceancode.cloud.test.ui.container.Dropdown;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

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
    public VxeTree tree(String nodeName) {
        String className = ".row--level-" + this.level;
        return new VxeTree(root(), root().container(className, nodeName), this, level + 1);
    }

    @Override
    public int getDepth() {
        return this.level;
    }

    @Override
    public VxeTree expand() {
        if (isExpand()) {
            return this;
        }
        expandIcon().click();
        load();
        return this;
    }

    private Locator expandIcon() {
        Locator it = container().locator("i");
        if (it.count() == 1) {
            return it;
        }
        return container().locator().locator(".vxe-icon-square-plus-fill");
    }

    @Override
    public VxeTree collapse() {
        if (!isExpand()) {
            return this;
        }
        expandIcon().click();
        load();
        return this;
    }

    @Override
    public boolean isExpand() {
        String className = ".row--level-" + this.level + 1;
        return root().locator().locator(className).count() > 0;
    }

    @Override
    public Dropdown menu() {
        container().contextmenu();
        return new Dropdown(root(), ()->root().locator(".vue-contextmenu-listWrapper"));
    }

    private void load() {
        UiUtil.waitForLoading(() -> {
            String className = ".vxe-table-icon-spinner";
            Locator it = container().locator().locator(className);
            return it.count() == 0 || !it.isVisible();
        });
    }
}
