package com.oceancode.cloud.test.ui.component;

import com.oceancode.cloud.test.ui.UiElement;
import com.oceancode.cloud.test.ui.container.UIContainer;

public class BaseComponent extends UiElement {
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
        return this.root;
    }

    @Override
    public int count() {
        return container().count();
    }

    @Override
    public boolean isVisible() {
        return container().isVisible();
    }
}
