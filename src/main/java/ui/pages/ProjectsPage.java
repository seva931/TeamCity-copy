package ui.pages;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectsPage extends AuthorizedPage<ProjectsPage> {
    @Override
    public String url() {
        return "/favorite/projects?mode=builds";
    }

    private SelenideElement projectById(String projectId) {
        return $x(String.format("//div[@data-test='subproject' and @data-project-id='%s']", projectId));
    }

    public ProjectsPage shouldContainProjectId(String projectId) {
        projectById(projectId).shouldBe(visible, Duration.ofSeconds(15));
        return this;
    }
}