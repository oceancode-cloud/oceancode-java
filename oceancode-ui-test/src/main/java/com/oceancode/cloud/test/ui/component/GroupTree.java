package com.oceancode.cloud.test.ui.component;

import com.oceancode.cloud.test.ui.container.UIContainer;

public class GroupTree extends Tree {
    public GroupTree(UIContainer root, UIContainer container) {
        super(root, container);
    }

    public GroupTree(UIContainer container) {
        super(container);
    }

    public String getTitle() {
        return container().container(".title").getText();
    }
}
