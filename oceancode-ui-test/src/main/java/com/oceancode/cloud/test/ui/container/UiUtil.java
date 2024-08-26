package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class UiUtil {
    private static Browser browser;
    private static Page page;
    private static Properties properties;

    private UiUtil() {
    }

    static {
        loadProperties();
        init();
    }

    public static String getFrameworkTag() {
        return getProperties().getProperty("oc.test.ui.framwork.tag", "el");
    }

    private static void init() {
        Map<String, String> map = new HashMap<>();
        map.put("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD", "1");
        map.put("PLAYWRIGHT_BROWSERS_PATH", getProperties().getProperty("PLAYWRIGHT_BROWSERS_PATH"));
        Playwright playwright = Playwright.create(new Playwright.CreateOptions().setEnv(map));
        BrowserType chromium = playwright.chromium();
        browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--start-maximized"))
                .setChannel(getProperties().getProperty("oc.test.ui.channel", "chrome")));
        page = browser.newPage();
    }

    private static void loadProperties() {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("src/test/resources/application-ui.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void waitForLoading(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Page getPage() {
        return page;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static UIContainer rootContainer() {
        return new UIContainer(null, getPage().locator("html"));
    }
}
