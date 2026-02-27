package ui;

import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.component.ProjectsSidebar;
import ui.pages.ProjectsPage;

import java.util.UUID;

import static com.codeborne.selenide.logevents.SelenideLogger.step;

@WebTest
@WithBuild
public class ProjectsUITest extends BaseUITest {

    @DisplayName("Позитивный тест: созданный по API проект отображается на UI")
    @Test
    public void createdProjectShouldBeVisibleOnUiTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        step("Открыть страницу проектов и проверить, что проект отображается в списке", () -> {
            new ProjectsPage()
                    .open()
                    .shouldContainProjectId(project.getId());
        });
    }

    @DisplayName("Позитивный тест: проект успешно находится через поиск в sidebar")
    @Test
    public void createdProjectShouldBeFoundBySearchInProjectsSidebarTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        step("Открыть страницу Projects, ввести имя проекта в поиск sidebar и проверить, что проект найден", () -> {
            new ProjectsPage().open();

            new ProjectsSidebar()
                    .searchProjectByName(project.getName())
                    .shouldContainProject(project.getName());
        });
    }

    @DisplayName("Негативный тест: для несуществующего проекта в поиске sidebar отображается предложение создать проект")
    @Test
    public void nonexistentProjectShouldSuggestCreateProjectInSidebarTest(
            @User CreateUserResponse user
    ) {
        String projectName = "nonexistent-project-" + UUID.randomUUID();

        step("Открыть страницу Projects, ввести несуществующее имя проекта в поиск sidebar и проверить" +
                " предложение создать проект", () -> {
            new ProjectsPage().open();

            new ProjectsSidebar()
                    .searchProjectByName(projectName)
                    .shouldShowCreateProjectSuggestion();
        });

    }
}