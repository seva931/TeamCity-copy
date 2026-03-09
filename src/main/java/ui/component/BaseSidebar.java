package ui.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public abstract class BaseSidebar<T extends BaseSidebar<T>> extends BaseComponent<T> {

    protected final SelenideElement toggleSidebarButton =
            self.$x(".//*[@data-hint-container-id='toggle-sidebar']");

    protected final SelenideElement searchButton =
            self.$("[data-test='sidebar-search']");

    protected BaseSidebar() {
        super($("[data-test='sidebar']"));
    }

}