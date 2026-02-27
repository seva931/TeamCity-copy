package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class ProjectDetailsPage extends ProjectPage<ProjectDetailsPage> {

    private SelenideElement createBuildTypeButton = $(By.xpath("(//*[@data-test='ring-dropdown']//*/button[@title='add'])[last()]"));
    private SelenideElement newBuildConfigurationButton = $(By.xpath("//*/span[contains(text(),'New build configuration')]/../.."));

    @Override
    public String url() {
        return "/project/MyProjectId1?mode=builds";
    }

    public String url(String projectId) {
        return String.format("/project/%s?mode=builds", projectId);
    }

    public ProjectDetailsPage open(String projectId) {
        return Selenide.open(url(projectId), ProjectDetailsPage.class);
    }

    public ProjectDetailsPage goToCreateBuildType() {
        createBuildTypeButton.click();
        newBuildConfigurationButton.click();
        return this;
    }
}