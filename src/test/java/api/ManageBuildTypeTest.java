package api;

import api.models.*;
import api.models.comparison.ModelAssertions;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.BuildManageSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.AtributesOfResponse;
import common.data.RoleId;
import common.generators.RandomModelGenerator;
import common.generators.TestDataGenerator;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import jupiter.annotation.meta.WithProject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTest
@WithProject
public class ManageBuildTypeTest extends BaseTest {

    @DisplayName("Позитивный тест: создание билд конфигурации")
    @Test
    public void userCreateBuildTypeTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        CreateBuildTypeRequest createBuildTypeRequest = RandomModelGenerator.builder(CreateBuildTypeRequest.class).withProjectId(project.getId()).build();

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        ModelAssertions.assertThatModels(createBuildTypeRequest, createBuildTypeResponse).match();

        CreateBuildTypeResponse createdBuild = BuildManageSteps.getAllBuildTypes().stream()
                .filter(build -> build.getId().equals(createBuildTypeResponse.getId())).findFirst().get();

        ModelAssertions.assertThatModels(createBuildTypeRequest, createdBuild).match();
    }

    @DisplayName("Негативный тест: создание билд конфигурации с именем уже созданной конфигурации")
    @Test
    public void userCanNotCreateBuildTypeWithSameNameTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        CreateBuildTypeRequest createFirstBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        CreateBuildTypeRequest createSecondBuildTypeRequest = RandomModelGenerator.builder(CreateBuildTypeRequest.class).withName(createFirstBuildTypeRequest.getName()).withProjectId(project.getId()).build();
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.badRequestWithErrorText(AtributesOfResponse.BUILD_CONFIGURATION_WITH_SUCH_NAME_ALREADY_EXISTS_ERROR.getFormatedText(createFirstBuildTypeRequest.getName(), project.getName())))
                .post(createSecondBuildTypeRequest);

        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createSecondBuildTypeRequest.getId()));

        assertFalse(isFind);
    }

    @DisplayName("Позитивный тест: получение информации о созданной билд конфигурации")
    @Test
    public void userGetInfoBuildTypeTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        GetInfoBuildTypeResponse getInfoBuildTypeResponse = new ValidatedCrudRequester<GetInfoBuildTypeResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.requestReturnsOk())
                .get(createBuildTypeRequest.getId());

        ModelAssertions.assertThatModels(createBuildTypeRequest, getInfoBuildTypeResponse).match();
    }

    @DisplayName("Негативный тест: получение информации о не существующей билд конфигурации")
    @Test
    public void userGetInfoAboutNotExistBuildTypeTest(@User CreateUserResponse user) {
        String buildId = TestDataGenerator.generateBuildId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(AtributesOfResponse.NO_BUILD_TYPE_ERROR.getFormatedText(buildId)))
                .get(buildId);
    }

    @DisplayName("Позитивный тест: получение информации о списке созданных билд конфигураций")
    @Test
    public void userGetInfoBuildTypeListTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        GetBuildListInfoResponse getBuildListInfoResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .get().extract().as(GetBuildListInfoResponse.class);

        softly.assertThat(getBuildListInfoResponse.getCount())
                .as("Поле count")
                .isNotNull();

        boolean isFind = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFind);
    }

    @DisplayName("Позитивный тест: удаление билд конфигурации")

    @Test
    public void userDeleteBuildTypeTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        boolean isFindCreatedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFindCreatedBuildType);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.noContent())
                .delete(createBuildTypeRequest.getId());

        boolean isFindDeletedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertFalse(isFindDeletedBuildType);
    }

    @DisplayName("Негативный тест: удаление несуществующей билд конфигурации")
    @Test
    public void userDeleteNotExistBuildTypeTest(@User CreateUserResponse user) {
        String buildId = TestDataGenerator.generateBuildId();

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.notFoundWithErrorText(AtributesOfResponse.NO_BUILD_TYPE_ERROR.getFormatedText(buildId)))
                .delete(buildId);
    }

    @DisplayName("Негативный тест: удаление билд конфигурации без прав админа")
    @Test
    public void userDeleteBuildTypeWithoutRulesTest(
            @User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        CreateBuildTypeRequest createBuildTypeRequest = BuildManageSteps.createBuildType(project.getId()).request();

        boolean isFindCreatedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFindCreatedBuildType);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_TYPES_ID,
                ResponseSpecs.forbiddenWithErrorText(AtributesOfResponse.YOU_DONT_HAVE_ENOUGH_PERMISSIONS_ERROR.getFormatedText(project.getId())))
                .delete(createBuildTypeRequest.getId());

        boolean isFindDeletedBuildType = BuildManageSteps.getAllBuildTypes().stream()
                .anyMatch(build -> build.getId().equals(createBuildTypeRequest.getId()));

        assertTrue(isFindDeletedBuildType);
    }
}
