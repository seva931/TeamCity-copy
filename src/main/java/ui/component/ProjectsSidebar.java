package ui.component;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;

public class ProjectsSidebar extends BaseSidebar<ProjectsSidebar> {

    private SelenideElement projectByName(String projectName) {
        return self.$x(String.format(".//*[@data-test-itemtype='project']//*[@aria-label='%s']", projectName));
    }

    private final SelenideElement createProjectSuggestionLink =
            self.$x(".//a[normalize-space()='Create project']");

    public ProjectsSidebar searchProjectByName(String projectName) {
        searchButton.setValue(projectName);
        return this;
    }

    public ProjectsSidebar shouldContainProject(String projectName) {
        projectByName(projectName).shouldBe(visible, Duration.ofSeconds(10));
        return this;
    }

    public ProjectsSidebar shouldShowCreateProjectSuggestion() {
        createProjectSuggestionLink.shouldBe(visible);
        return this;
    }
}