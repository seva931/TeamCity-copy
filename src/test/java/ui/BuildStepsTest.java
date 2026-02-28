package ui;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.steps.BuildStepsSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.BuildStepPropertyData;
import common.data.BuildStepTypeData;
import common.generators.RandomModelGenerator;
import common.generators.TestDataGenerator;
import jdk.jfr.Description;
import jupiter.annotation.Build;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import jupiter.annotation.meta.WithProject;
import org.junit.jupiter.api.Test;
import ui.pages.BuildStepsSettingsPage;

import java.util.List;

import static com.codeborne.selenide.logevents.SelenideLogger.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WithProject
@WebTest
@WithBuild
public class BuildStepsTest extends BaseUITest{

    private String buildStepName;
    private AddBuildStepResponse addBuildStepResponse;

    @Description("Позитивный тест. Создание билд степа с корректными данными")
    @Test
    public void userCanCreateBuildStepTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project,
                                           @Build CreateBuildTypeResponse buildType) {
        step("Подготовить степ", () -> {
            buildStepName = TestDataGenerator.generateBuildStepName();
        });

        step("Создать степ и проверить ui", () -> {
            new BuildStepsSettingsPage()
                    .open(buildType.getId())
                    .createBuildStep(buildStepName)
                    .checkStepInList(buildStepName);
        });

        step("Проверить создание степа по api", () -> {
            boolean isFind = BuildStepsSteps.getAllSteps(user, buildType)
                    .getStep()
                    .stream()
                    .anyMatch(step -> step.getName().equals(buildStepName));

            assertTrue(isFind);
        });
    }

    @Description("Негативный тест. Создание билд степа с уже существующим id")
    @Test
    public void userCantCreateBuildStepWithSameIdTest(@User CreateUserResponse user,
                                           @Project ProjectResponse project,
                                           @Build CreateBuildTypeResponse buildType) {
        step("Подготовить существующий степ", () -> {
            buildStepName = TestDataGenerator.generateBuildStepName();
            BuildStepProperties properties = BuildStepProperties.builder().property(
                    List.of(
                            BuildStepProperty.builder()
                                    .name(BuildStepPropertyData.SCRIPT_CONTENT.getName())
                                    .value("echo Hello")
                                    .build())
            ).build();

            AddBuildStepRequest addBuildStepRequest = RandomModelGenerator.generate(AddBuildStepRequest.class);
            addBuildStepRequest.setType(BuildStepTypeData.SIMPLE_RUNNER.getType());
            addBuildStepRequest.setProperties(properties);

            addBuildStepResponse = new CrudRequester(
                    RequestSpecs.authAsUser(user),
                    Endpoint.BUILD_TYPES_ID_STEPS,
                    ResponseSpecs.ok()
            ).post(buildType.getId(), addBuildStepRequest)
                    .extract().as(AddBuildStepResponse.class);
        });

        step("Заполненить поля для создания степа и проверить, что на ui ошибка создания", () -> {
            new BuildStepsSettingsPage()
                    .open(buildType.getId())
                    .createBuildStep(addBuildStepResponse.getId())
                    .checkCreateStepError(addBuildStepResponse.getId());
        });

        step("Проверить по api, что с таким id толкьо один степ", () -> {
            long countSteps = BuildStepsSteps.getAllSteps(user, buildType)
                    .getStep()
                    .stream()
                    .filter(step -> step.getId().equals(addBuildStepResponse.getId()))
                    .count();

            assertEquals(1, countSteps);
        });
    }

}
