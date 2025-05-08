package com.oceancode.cloud.test.ui.component;

import com.oceancode.cloud.test.ui.container.Dropdown;
import com.oceancode.cloud.test.ui.container.UIContainer;
import com.oceancode.cloud.test.ui.container.UiUtil;

import java.util.Objects;

public class Tree extends BaseComponent {
    private Tree parent;

    public Tree(UIContainer container, Tree parent) {
        super(container);
        this.parent = parent;
    }

    public Tree(UIContainer root, UIContainer container) {
        super(root, container);
    }

    public Tree(UIContainer root, UIContainer container, Tree parent) {
        super(root, container);
        this.parent = parent;
    }

    public Tree(UIContainer container) {
        super(container);
    }

    public Tree tree(String nodeName) {
        return null;
    }

    public Tree tress(String... childrenNodes) {
        Tree tree = Objects.nonNull(parent()) ? this : null;
        if (Objects.nonNull(childrenNodes)) {
            for (int i = 0; i < childrenNodes.length; i++) {
                if (Objects.isNull(tree)) {
                    tree = this.tree(childrenNodes[i]);
                    UiUtil.waitForLoading(() -> container().isVisible());
                    continue;
                }
                tree.expand();
                tree = tree.tree(childrenNodes[i]);
                UiUtil.waitForLoading(() -> container().isVisible());
            }
        }

        return tree;
    }

    public Tree parent() {
        return this.parent;
    }

    public boolean hasParent() {
        return Objects.nonNull(parent());
    }

    public int getDepth() {
        return 0;
    }

    public Tree collapse() {
        return null;
    }

    public Tree expand() {
        return null;
    }

    public boolean isExpand() {
        return false;
    }

    public Dropdown menu() {
        return null;
    }
}
