package ui.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class LeftNavigationMenu {

    private final SelenideElement administrationButton = $x("//span[@data-test-title='Administration']");

    private final SelenideElement queueButton = $x("//span[contains(@data-test-title,'Queue')]");

    private final SelenideElement agentsButton = $x("//a[@href='/agents']/parent::span");

    private final SelenideElement changesButton = $x("//span[@data-test-title='Changes']");

    private final SelenideElement projectsButton = $x("//span[@data-test-title='Projects']");

    private final SelenideElement teamCityButton = $x("//span[@data-test-title='TeamCity']");

    private final SelenideElement myInvestigationsButton =
            $x("//span[starts-with(@data-test-title,'My Investigations')]");

    private final SelenideElement whatsNewButton = $x("//span[@data-test-title=\"What's New\"]");

    private final SelenideElement helpButton = $x("//span[@data-test-title='Help']");

    private final SelenideElement adminSidebarButton =
            $x("//span[@data-test='avatar']/ancestor::button[1]");

    public SelenideElement queueButton() {
        return queueButton;
    }

    public SelenideElement agentsButton() {
        return agentsButton;
    }

    public SelenideElement changesButton() {
        return changesButton;
    }

    public SelenideElement projectsButton() {
        return projectsButton;
    }

    public SelenideElement teamCityButton() {
        return teamCityButton;
    }

    public SelenideElement myInvestigationsButton() {
        return myInvestigationsButton;
    }

    public SelenideElement whatsNewButton() {
        return whatsNewButton;
    }

    public SelenideElement adminButton() {
        return administrationButton;
    }

    public SelenideElement helpButton() {
        return helpButton;
    }

    public SelenideElement adminSidebarButton() {
        return adminSidebarButton;
    }

    public LeftNavigationMenu checkAllButtonsVisible() {
        administrationButton.shouldBe(Condition.visible);
        queueButton.shouldBe(Condition.visible);
        agentsButton.shouldBe(Condition.visible);
        changesButton.shouldBe(Condition.visible);
        projectsButton.shouldBe(Condition.visible);
        teamCityButton.shouldBe(Condition.visible);
        myInvestigationsButton.shouldBe(Condition.visible);
        whatsNewButton.shouldBe(Condition.visible);
        helpButton.shouldBe(Condition.visible);
        adminSidebarButton.shouldBe(Condition.visible);
        return this;
    }
}