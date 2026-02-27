package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class BuildTypePage extends ProjectPage<BuildTypePage> {

    private SelenideElement buildTypeNameInList(String buildName){
        return $(By.xpath(String.format("//*[@aria-selected='true']//*[contains(text(),'%s')]", buildName)));
    }

    @Override
    public String url() {
        return "/buildConfiguration/MyProjectId1_NewNAme?buildTypeTab=";
    }

    public String url(String projectId, String buildName) {
        return String.format("/buildConfiguration/%s_%s?buildTypeTab=", projectId, buildName);
    }

    public BuildTypePage open(String projectId, String buildName) {
        String normalizedBuildName = buildName.substring(0, 1).toUpperCase() + buildName.substring(1);
        return Selenide.open(url(projectId, normalizedBuildName), BuildTypePage.class);
    }

    public BuildTypePage checkCreatedBuildType(String buildName) {
        buildTypeNameInList(buildName).shouldBe(Condition.visible);
        return this;
    }
}