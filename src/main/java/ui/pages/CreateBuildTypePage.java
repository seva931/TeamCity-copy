package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends ProjectPage<CreateBuildTypePage> {
    private SelenideElement newBuildConfigurationNameInput = $(By.xpath("//*[contains(text(),'Name')]/..//*/input"));
    private SelenideElement saveChangesButton = $(By.xpath("//*/button[contains(text(),'Create')]"));
    private SelenideElement saveChangesDisabledButton = $(By.xpath("//*/button[@disabled='' and contains(text(),'Create')]"));

    @Override
    public String url() {
        return "/projects/create?projectId=MyProjectId1&setup=build";
    }

    public String url(String projectId) {
        return String.format("/projects/create?%s=MyProjectId1&setup=build", projectId);
    }

    public CreateBuildTypePage createBuildTypePage(String buildName){
        newBuildConfigurationNameInput.append(buildName);
        saveChangesButton.click();
        return this;
    }

    public CreateBuildTypePage checkAlert(String buildName, String projectName){
        String errorMessage = String.format("Build configuration with name \"%s\" already exists in project: \"%s\"", buildName, projectName);
        SelenideElement noticeErrorPopup = $(Selectors.byText(errorMessage));
        //SelenideElement noticeErrorPopup = $(By.xpath("//*[contains(text(),'" + errorMessage + "')]"));
        noticeErrorPopup.shouldBe(Condition.visible);
        return this;
    }

    public CreateBuildTypePage checkDisableButtonCreate(){
        saveChangesDisabledButton.shouldBe(Condition.visible);
        return this;
    }
}