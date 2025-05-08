package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.oceancode.cloud.common.util.ValueUtil;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

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
        String property = getProperties().getProperty("oc.test.ui.headless.args", "--start-maximized");
        List<String> list = Arrays.stream(property.split(",")).toList();
        browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(list)
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

    public static void waitForLoading(Supplier<Boolean> supplier) {
        waitForLoading(supplier, 10);
    }

    public static void waitForLoading(Supplier<Boolean> supplier, int maxSeconds) {
        long startTime = System.currentTimeMillis();
        int exceptionCount = 0;
        try {
            while (true) {
                try {
                    if (ValueUtil.isTrue(supplier.get())) {
                        break;
                    }
                } catch (Throwable throwable) {
                    exceptionCount++;
                    System.err.println(throwable);
                }
                Thread.sleep(100);
                if (exceptionCount > 100) {
                    break;
                }
                if (System.currentTimeMillis() - startTime > maxSeconds * 1000L) {
                    break;
                }
                try {
                    if (ValueUtil.isTrue(supplier.get())) {
                        break;
                    }
                } catch (Throwable throwable) {
                    exceptionCount++;
                    System.err.println(throwable);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static Page getPage() {
        return page;
    }

    public static String containClass(String name) {
        return "//*[contains(@class,'" + name + "')]";
    }

    public static Properties getProperties() {
        return properties;
    }

    public static UIContainer rootContainer() {
        return new UIContainer(null, () -> getPage().locator("html"));
    }

    public static Locator findLocator(UIContainer container, Function<Locator, Locator> function) {
        Locator it = function.apply(container.locator());
        if (Objects.isNull(it) || it.count() == 0) {
            if (Objects.nonNull(container.parent())) {
                it = function.apply(container.parent().locator());
            }
        }
        if (Objects.nonNull(it) && it.count() > 1) {
            List<Locator> list = it.all().stream().filter(e -> e.isVisible()).toList();
            if (list.size() == 1) {
                it = list.get(0);
                return it;
            }
        }
        if (Objects.isNull(it) || it.count() == 0 || !it.isVisible()) {
            it = function.apply(UiUtil.rootContainer().locator());
        }
        if (Objects.isNull(it)) {
            return null;
        }
        return it.all().stream().filter(e -> e.isVisible()).findFirst().orElse(null);
    }

    public static <T extends UIContainer> T get(Supplier<T> supplier) {
        UiUtil.waitForLoading(() -> supplier.get().count() > 1 || (supplier.get().count() == 1 && supplier.get().isVisible()));
        return supplier.get();
    }
}
