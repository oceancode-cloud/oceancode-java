package com.oceancode.cloud.test.ui.component;

import com.oceancode.cloud.test.ui.container.Dropdown;
import com.oceancode.cloud.test.ui.container.UIContainer;

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

    public Tree parent() {
        return this.parent;
    }

    public boolean hasParent() {
        return Objects.nonNull(parent());
    }

    public int getDepth() {
        return 0;
    }

    public void collapse() {

    }

    public void expand() {
    }

    public boolean isExpand() {
        return false;
    }

    public Dropdown menu() {
        return null;
    }
}
