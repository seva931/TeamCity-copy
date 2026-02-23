package ui;

import api.models.CreateBuildTypeRequest;
import api.models.CreateUserResponse;
import api.models.ProjectResponse;
import api.requests.steps.BuildManageSteps;
import common.generators.TestDataGenerator;
import jdk.jfr.Description;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WithProject;
import jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Test;
import ui.pages.BuildTypePage;
import ui.pages.CreateBuildTypePage;
import ui.pages.ProjectsPage;

import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static org.junit.jupiter.api.Assertions.*;

@WithProject
@WebTest
public class ManageBuildTypeTest extends BaseUITest{
    String buildName;
    CreateBuildTypeRequest createFirstBuildTypeRequest;

    @Description("Позитивный тест. Создание билд конфигурации с корректными данными")
    @Test
    public void userCanCreateBuildTypeTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project) {
        step("Подготовить билд", () -> {
            buildName = TestDataGenerator.generateBuildName();
        });

        step("Создать билд конфигурацию", () -> {
            new ProjectsPage()
                    .open(project.getId())
                    .goToCreateBuildType()
                    .getPage(CreateBuildTypePage.class)
                    .createBuildTypePage(buildName)
                    .getPage(BuildTypePage.class)
                    .open(project.getId(), buildName)
                    .checkCreatedBuildType(buildName);
        });

        step("Проверка, что билд создан через api", () -> {
            boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                    .anyMatch(build -> build.getName().equals(buildName));

            assertTrue(isFind);
        });
    }

    @Description("Негативный тест. Создание билд конфигурации именем уже созданной конфигурации")
    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest(@User CreateUserResponse user,
                                                          @Project ProjectResponse project) {
        step("Подготовить билд конфигурацию", () -> {
            createFirstBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();
        });

        step("Создать билд конфигурацию с тем же именем", () -> {
            new ProjectsPage()
                    .open(project.getId())
                    .goToCreateBuildType()
                    .getPage(CreateBuildTypePage.class)
                    .createBuildTypePage(createFirstBuildTypeRequest.getName())
                    .checkAlert(createFirstBuildTypeRequest.getName(), project.getName());
        });

        step("Проверка, что существует только один билд с таким именем через api", () -> {
            long count = BuildManageSteps.getAllBuildTypes().stream()
                    .filter(build -> build.getName().equals(createFirstBuildTypeRequest.getName()))
                    .count();

            assertEquals(1, count);
        });
    }

    @Description("Негативный тест. Невозможно создать конфигурацию с пустым именем (кнопка создания задизейблена)")
    @Test
    public void userCanNotCreateBuildTypeWithEmptyNameTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project) {
        new ProjectsPage()
                .open(project.getId())
                .goToCreateBuildType()
                .getPage(CreateBuildTypePage.class)
                .checkDisableButtonCreate();
    }
}
