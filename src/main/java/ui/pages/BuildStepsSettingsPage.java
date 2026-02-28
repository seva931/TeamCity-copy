package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.data.AtributesOfResponse;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class BuildStepsSettingsPage extends BasePage<BuildStepsSettingsPage>{

    private SelenideElement addBuildStepsButton = $x("//*[contains(text(), \"Add build step\")]/..");
    private SelenideElement intelliJIDEAProjectItem = $x("//*[contains(text(), \"IntelliJ IDEA Project\")]/..");
    private SelenideElement buildStepNameField = $x("//*[@id=\"buildStepName\"]");
    private SelenideElement buildStepIdField = $x("//*[@id=\"newRunnerId\"]");
    private SelenideElement saveButton = $x("//*[@name=\"submitButton\"]");

    private SelenideElement buildStepNameInTable(String stepName){
        return $x(String.format("//*[@class=\"highlightable parametersTable\"]//*[contains(text(), \"%s\")]", stepName));
    }

    private SelenideElement buildStepNameError(String stepName){
        return $x(String.format("//*[contains(text(), \"%s\")]", AtributesOfResponse.BUILD_STEP_WITH_THE_SPECIFIED_ID_ALREADY_EXIST.getFormatedText(stepName)));
    }

    @Override
    public String url() {
        return "";
    }

    public String url(String buildId) {
        return String.format("/admin/editBuildRunners.html?id=buildType:%s", buildId);
    }

    public BuildStepsSettingsPage open(String buildId) {
        return Selenide.open(url(buildId), BuildStepsSettingsPage.class);
    }

    public BuildStepsSettingsPage createBuildStep(String stepName){
        addBuildStepsButton.click();
        intelliJIDEAProjectItem.click();
        buildStepNameField.setValue(stepName);
        buildStepIdField.setValue(stepName);
        saveButton.click();
        return this;
    }

    public BuildStepsSettingsPage checkStepInList(String stepName){
        buildStepNameInTable(stepName).shouldBe(Condition.visible);
        return this;
    }

    public BuildStepsSettingsPage checkCreateStepError(String stepName){
        buildStepNameError(stepName).shouldBe(Condition.visible);
        return this;
    }
}
