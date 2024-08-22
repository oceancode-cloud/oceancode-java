package com.oceancode.cloud.test.ui.page;

import com.oceancode.cloud.test.ui.container.UIContainer;

public class BasePage {
    private UIContainer container;

    public BasePage(UIContainer container) {
        this.container = container;
    }

    public void load() {

    }

    public UIContainer container(){
        return this.container;
    }

    public static void waitForLoading(int second){
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
