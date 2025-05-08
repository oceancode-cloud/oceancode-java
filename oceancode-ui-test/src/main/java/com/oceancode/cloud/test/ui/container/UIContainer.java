package com.oceancode.cloud.test.ui.container;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.MouseButton;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.test.ui.component.impl.VxeTree;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

public class UIContainer {
    private Page page;
    private UIContainer parent;
    private Supplier<Locator> getFunction;

    public UIContainer(UIContainer parent, Supplier<Locator> getFunction) {
        this.page = UiUtil.getPage();
        this.parent = parent;
        this.getFunction = getFunction;
    }

    public Page page() {
        return this.page;
    }

    public UIContainer parent() {
        return this.parent;
    }

    public Locator locator() {
        return this.getFunction.get();
    }

    public Locator locator(String selector) {
        return this.locator().locator(selector);
    }

    public UIContainer container(String selector, String label) {
        Supplier<Locator> supplier = () -> {
            Locator loc = this.locator(selector);
            if (ValueUtil.isNotEmpty(label)) {
                loc = loc.filter(new Locator.FilterOptions().setHasText(label));
            }
            return loc;
        };

        return new UIContainer(this, supplier);
    }

    public UIContainer container(String selector) {
        return container(selector, null);
    }

    public Form form() {
        return new Form(this, () -> locator().locator("form"));
    }

    protected String getClassName(String className) {
        return "." + UiUtil.getFrameworkTag() + "-" + className;
    }

    public UIContainer button(String label) {
        return new UIContainer(this, () -> locator().locator("button").filter(new Locator.FilterOptions().setHasText(label)));
    }

    public UIContainer button() {
        return new UIContainer(this, () -> locator().locator("button"));
    }

    public int count() {
        return locator().count();
    }

    public void click() {
        locator().click();
        load();
    }

    public UIContainer findByText(String text) {
        return new UIContainer(this, () -> locator().getByText(text));
    }

    public UIContainer findByTitle(String text) {
        return findByAttr("title", text);
    }

    public UIContainer findByAttr(String attr, String value) {
        if (value instanceof String) {
            value = "\"" + value + "\"";
        }
        String finalValue = value;
        return new UIContainer(this, () -> locator().locator("*[" + attr + "=" + finalValue + "]"));
    }

    public Dialog dialog(String title) {
        return new Dialog(this, () -> {
            Locator loc = page.locator("div[role=dialog]");
            if (ValueUtil.isNotEmpty(title)) {
                loc = loc.filter(new Locator.FilterOptions().setHasText(title));
            }
            if (loc.count() > 1) {
                loc = loc.all().stream().filter(e -> e.isVisible()).findFirst().orElse(null);
            }
            return loc;
        });
    }

    public Dialog dialog() {
        return dialog(null);
    }

    public String getText() {
        return locator().innerText();
    }

    public void contextmenu() {
        locator().click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
    }

    public boolean exists() {
        return locator().count() != 0;
    }

    public Message message() {
        return new Message(this, () -> parent().locator("div[class$=-message"));
    }

    public Input input(String selector) {
        return new Input(this, () -> locator(selector).first());
    }

    public Input input() {
        return input("input");
    }

    public Menu menu() {
        Supplier<Locator> supplier = () -> {
            Locator it = locator().getByRole(AriaRole.MENU);
            if (it.count() == 1) {
                return it;
            }
            it = locator().getByRole(AriaRole.MENUITEM).first();
            if (it.count() == 1) {
                String className = it.getAttribute("class");
                String menuClass = Arrays.stream(className.split(" ")).filter(e -> e.contains("menu-item")).findFirst().orElse(null);
                if (ValueUtil.isNotEmpty(menuClass)) {
                    String menuClassName = menuClass.substring(0, menuClass.lastIndexOf("-"));
                    return locator("." + menuClassName);
                }
            }
            return locator();
        };

        return new Menu(this, supplier);
    }

    public List list() {
        return new List(this, () -> locator().locator("*[class$=-list]"));
    }

    public SliderBar sliderBar() {

        return new SliderBar(this, () -> {
            Locator sliderBar = locator().locator("aside");
            if (sliderBar.count() != 1) {
                sliderBar = locator().locator(".slider-bar");
            }
            if (sliderBar.count() == 0) {
                sliderBar = locator().locator("*[class$=-slider-bar]");
            }
            return sliderBar;
        });
    }

    public SliderBar sliderBar(String selector) {
        return new SliderBar(this, () -> locator().locator(selector));
    }

    public VxeTree vxeTree() {
        return new VxeTree(this, this);
    }

    public Select select() {
        return new Select(this, () -> locator().locator(UiUtil.containClass("-select")).first());
    }


    public Dropdown dropdown() {
        return new Dropdown(this, () -> locator().locator(UiUtil.containClass("dropdown")));
    }

    public Dropdown dropdown(String text) {
        return new Dropdown(this, () -> locator().locator(UiUtil.containClass("dropdown"), new Locator.LocatorOptions().setHasText(text)));
    }

    public Image image() {
        return new Image(this, () -> locator().locator("img"));
    }

    public Popover popover() {
        String className = UiUtil.containClass("__popper");
        return new Popover(this, () -> UiUtil.findLocator(this, loc -> loc.locator(className)));
    }

    public UIContainer header() {
        return new UIContainer(this, () -> {
            Locator it = locator().locator("header");
            if (it.count() == 0) {
                it = locator().locator(UiUtil.containClass("-header")).first();
            }
            return it;
        });
    }

    public Tabs tabs() {
        return new Tabs(this, () -> locator().locator(UiUtil.containClass("-tabs")));
    }

    public Collapse collapse() {
        return new Collapse(this, () -> locator().locator(UiUtil.containClass("-collapse")));
    }

    public RadioGroup radioGroup() {
        return new RadioGroup(this, () -> locator().locator(UiUtil.containClass("-radio-group")));
    }

    public Table table() {
        return new Table(this, () -> {
            Locator it = locator().locator(UiUtil.containClass("-table")).first();
            if (it.count() == 0) {
                it = locator("table");
            }
            return it;
        });
    }

    public Checkbox checkbox() {
        return new Checkbox(this, () -> locator().locator(UiUtil.containClass("-checkbox")));
    }

    public double width() {
        return locator().boundingBox().width;
    }

    public double height() {
        return locator().boundingBox().height;
    }

    public double x() {
        return locator().boundingBox().x;
    }

    public double y() {
        return locator().boundingBox().y;
    }

    public void dragIn(double x, double y, UIContainer container) {
        page.mouse().move(container.x() + container.width() / 2, container.y() + container.height() / 2);
        page.mouse().down();
        page.mouse().move(x, y);
        page.mouse().up();
    }

    public void dragInCenter(UIContainer container) {
        dragIn(x() + width() / 2, y() + height() / 2, container);
    }

    public void dragIn(UIContainer container) {
        Random random = new Random();
        double x = random.nextDouble(x(), x() + width() - container.width());
        double y = random.nextDouble(y(), y() + height() - container.height());

        dragIn(x, y, container);
    }

    public Graph graph() {
        return new Graph(this, () -> locator().locator(UiUtil.containClass("-graph-svg")).first());
    }

    public void mouseDown() {
        page.mouse().move(x() + width() / 2, y() + height() / 2);
        page.mouse().down();
    }

    public void mouseUp() {
        page.mouse().up();
    }

    public void mouseMove(double x, double y) {
        page.mouse().move(x, y);
    }

    public Card card(String title) {
        return new Card(this, () -> {
            Locator it = locator().locator(UiUtil.containClass("-card"));
            if (ValueUtil.isNotEmpty(title)) {
                it = it.all().stream().filter(e -> {
                    Locator temp = e.locator(UiUtil.containClass("-card-head"));
                    if (temp.count() == 1) {
                        return temp.getByText(title).count() > 0;
                    }
                    return e.getByText(title).count() > 0;
                }).findFirst().orElse(null);
            }
            return it;
        });
    }

    public void load() {

    }

    public boolean isVisible() {
        return locator().isVisible();
    }

    public void waitFor() {
        waitFor(() -> locator().isVisible());
    }

    public void waitFor(int seconds) {
        UiUtil.waitForLoading(seconds);
    }

    public void waitFor(UIContainer container) {
        UiUtil.waitForLoading(() -> container.isVisible());
    }

    public void waitFor(Supplier<Boolean> supplier) {
        UiUtil.waitForLoading(supplier);
    }

    public UIContainer first() {
        return new UIContainer(this, () -> locator().first());
    }

    public boolean isDisabled() {
        return locator().isDisabled();
    }
}
