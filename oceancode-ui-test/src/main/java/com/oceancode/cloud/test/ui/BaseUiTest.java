package com.oceancode.cloud.test.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.oceancode.cloud.test.base.BaseTest;
import com.oceancode.cloud.test.ui.container.UIContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseUiTest extends BaseTest {
    private Browser browser;
    private Page page;
    private UIContainer container;

    public BaseUiTest() {

    }

    protected void init() {
        Map<String, String> map = new HashMap<>();
        map.put("PLAYWRIGHT_SKIP_BROSER_DOWNLOAD", "1");
//        map.put("PLAYWRIGHT_BROWSERS_PATH","")
        Playwright playwright = Playwright.create(new Playwright.CreateOptions().setEnv(map));
        BrowserType chromium = playwright.chromium();
        browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--start-maximized"))
                .setChannel("chrom"));
        page = browser.newPage();
        container = new UIContainer(page, null, page.locator("html"));
    }
}
