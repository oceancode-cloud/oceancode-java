package com.oceancode.cloud.test.ui.component;

import com.oceancode.cloud.test.ui.container.UIContainer;

public class BaseComponent {
    private UIContainer container;
    private UIContainer root;

    public BaseComponent(UIContainer root, UIContainer container) {
        this.root = root;
        this.container = container;
    }

    public BaseComponent(UIContainer container) {
        this(container, container);
    }

    public UIContainer container() {
        return this.container;
    }

    public UIContainer root() {
        return this.container;
    }
}
