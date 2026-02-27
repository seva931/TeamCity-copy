package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class ProjectsPage extends AuthorizedPage<ProjectsPage> {

    private final SelenideElement buildNumberSearchInput = $x("//*[@id='headerSearchField']");

    @Override
    public String url() {
        return "/favorite/projects?mode=builds";
    }

    private ElementsCollection projects() {
        return $$x("//div[@data-test='subproject']");
    }

    private SelenideElement projectById(String projectId) {
        return $x(String.format("//div[@data-test='subproject' and @data-project-id='%s']", projectId));
    }

    public ProjectsPage shouldContainProjectId(String projectId) {
        projectById(projectId).shouldBe(visible, Duration.ofSeconds(15));
        return this;
    }

    public List<String> visibleProjectIds() {
        return projects().asFixedIterable().stream()
                .map(e -> e.getAttribute("data-project-id"))
                .filter(Objects::nonNull)
                .toList();
    }

    public ProjectsPage searchBuildByNumber(String buildNumber) {
        buildNumberSearchInput.setValue(buildNumber);
        return this;
    }
}