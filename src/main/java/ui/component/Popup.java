package ui.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class Popup extends BaseComponent<Popup> {
    public Popup(SelenideElement self) {
        super(self);
    }

    public Popup() {
        super($("[data-test='ring-popup']"));
    }

    public Popup shouldBeOpened() {
        self.shouldBe(visible);
        return this;
    }

    public Popup addComment(String comment) {
        self.$("textarea.ring-input-input").sendKeys(comment);
        return this;
    }

    public Popup submit() {
        self.$("button[type='submit']").click();
        return this;
    }

    public Popup cancel() {
        self.$("[data-test='cancel']").click();
        return this;
    }
}
