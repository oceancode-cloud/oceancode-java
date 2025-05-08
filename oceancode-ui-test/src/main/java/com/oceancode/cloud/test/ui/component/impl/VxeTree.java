package com.oceancode.cloud.test.ui.component.impl;

import com.microsoft.playwright.Locator;
import com.oceancode.cloud.test.ui.component.Tree;
import com.oceancode.cloud.test.ui.container.Dropdown;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

import java.util.Objects;

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
        Locator it = container().locator("i").all().stream().filter(e -> {
            String classNames = e.getAttribute("class");
            return classNames.contains("-caret-right");
        }).findFirst().orElseGet(null);
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
        Locator it = container().locator("i").all().stream().filter(e -> {
            String classNames = e.getAttribute("class");
            return classNames.contains("-caret-right") && classNames.contains("rotate90");
        }).findFirst().orElseGet(null);
        return Objects.nonNull(it);
    }

    @Override
    public Dropdown menu() {
        container().contextmenu();
        return new Dropdown(root(), () -> root().locator(".vue-contextmenu-listWrapper"));
    }

    private void load() {
        UiUtil.waitForLoading(() -> {
            String className = ".vxe-table-icon-spinner";
            Locator it = container().locator().locator(className);
            return it.count() == 0 || !it.isVisible();
        });
    }

    @Override
    public VxeTree tress(String... childrenNodes) {
        UiUtil.waitForLoading(() -> {
            Locator locator = expandIcon();
            return Objects.nonNull(locator) && locator.isVisible();
        });
        return (VxeTree) super.tress(childrenNodes);
    }
}
