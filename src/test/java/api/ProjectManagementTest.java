package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.AtributesOfResponse;
import common.generators.RandomModelGenerator;
import io.restassured.http.ContentType;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ApiTest
@WithBuild
public class ProjectManagementTest extends BaseTest {

    private static final String NOT_EXISTS_ID = "PRJ_NOT_EXISTS_404";

    @DisplayName("Позитивный тест: создание проекта")
    @Test
    public void userCreateProjectTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        ProjectListResponse list = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.ok()
        ).get()
                .extract().as(ProjectListResponse.class);

        boolean existsInList = list.getProject() != null && list.getProject().stream()
                .anyMatch(p -> project.getId().equals(p.getId()));

        softly.assertThat(existsInList)
                .as("Проект присутствует в списке проектов по id")
                .isTrue();

        ProjectResponse byId = new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(project.getId());

        softly.assertThat(byId.getId()).as("GET by id: поле id").isEqualTo(project.getId());
        softly.assertThat(byId.getName()).as("GET by id: поле name").isEqualTo(project.getName());
    }

    @DisplayName("Негативный тест: создание проекта с тем же id")
    @Test
    public void userCanNotCreateProjectWithSameIdTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        String secondName = (project.getName() + "_second").toLowerCase();

        CreateProjectRequest duplicateRequest = RandomModelGenerator.builder(CreateProjectRequest.class)
                .with("id", project.getId())
                .withName(secondName)
                .with("parentProject", ParentProject.root())
                .build();

        ErrorResponse errorResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.badRequest()
        ).post(duplicateRequest)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrors())
                .as("errors в ответе")
                .isNotNull()
                .isNotEmpty();

        ProjectResponse byId = new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(project.getId());

        softly.assertThat(byId.getName()).as("Имя проекта не изменилось").isEqualTo(project.getName());
    }

    @DisplayName("Позитивный тест: получение списка проектов")
    @Test
    public void userGetProjectsListTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        ProjectListResponse list = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.ok()
        ).get()
                .extract().as(ProjectListResponse.class);

        softly.assertThat(list.getCount()).as("Поле count").isNotNull().isGreaterThan(0);
        softly.assertThat(list.getProject()).as("Поле project").isNotNull().isNotEmpty();

        boolean exists = list.getProject().stream().anyMatch(p -> project.getId().equals(p.getId()));

        softly.assertThat(exists)
                .as("Список содержит созданный проект по id")
                .isTrue();
    }

    @DisplayName("Позитивный тест: получение информации о проекте по id")
    @Test
    public void userGetProjectByIdTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        ProjectResponse byId = new ValidatedCrudRequester<ProjectResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(project.getId());

        softly.assertThat(byId.getId()).as("Поле id").isEqualTo(project.getId());
        softly.assertThat(byId.getName()).as("Поле name").isEqualTo(project.getName());
    }

    @DisplayName("Негативный тест: получение информации о проекте по несуществующему id")
    @Test
    public void userGetProjectByNotExistIdTest(@User CreateUserResponse user) {
        ErrorResponse errorResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(NOT_EXISTS_ID)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse)
                .as("Тело ответа ошибки не null")
                .isNotNull();

        assertThat(errorResponse.getErrors())
                .as("Список errors")
                .isNotNull()
                .hasSize(1)
                .filteredOn(e -> e.getMessage().equals(
                        AtributesOfResponse.NO_PROJECT_FOUND_BY_ID_ERROR.getFormatedText(
                                NOT_EXISTS_ID,
                                NOT_EXISTS_ID
                        )))
                .hasSize(1);
    }

    @DisplayName("Позитивный тест: обновление параметра name проекта")
    @Test
    public void userUpdateProjectNameTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        String updatedName = (project.getName() + "_updated").toLowerCase();

        String responseBody = new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).put(project.getId(), updatedName)
                .extract()
                .asString();

        softly.assertThat(responseBody)
                .as("PUT /parameters/name: тело ответа")
                .isEqualTo(updatedName);

        String nameParam = new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.TEXT),
                Endpoint.PROJECT_NAME,
                ResponseSpecs.requestReturnsOk()
        ).get(project.getId())
                .extract()
                .asString();

        softly.assertThat(nameParam)
                .as("GET /parameters/name возвращает обновлённое значение")
                .isEqualTo(updatedName);
    }

    @DisplayName("Позитивный тест: удаление проекта")
    @Test
    public void userDeleteProjectTest(
            @User CreateUserResponse user,
            @Project ProjectResponse project
    ) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.noContent()
        ).delete(project.getId());

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).get(project.getId());

        ProjectListResponse list = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECTS,
                ResponseSpecs.ok()
        ).get()
                .extract().as(ProjectListResponse.class);

        boolean existsInList = list.getProject() != null && list.getProject().stream()
                .anyMatch(p -> project.getId().equals(p.getId()));

        softly.assertThat(existsInList)
                .as("Удалённый проект отсутствует в списке проектов по id")
                .isFalse();
    }

    @DisplayName("Негативный тест: удаление несуществующего проекта")
    @Test
    public void userDeleteNotExistProjectTest(@User CreateUserResponse user) {
        ErrorResponse errorResponse = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.PROJECT_ID,
                ResponseSpecs.notFound()
        ).delete(NOT_EXISTS_ID)
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse)
                .as("Тело ответа ошибки не null")
                .isNotNull();

        assertThat(errorResponse.getErrors())
                .as("Список errors")
                .isNotNull()
                .hasSize(1)
                .filteredOn(e -> e.getMessage().equals(
                        AtributesOfResponse.NO_PROJECT_FOUND_BY_ID_ERROR.getFormatedText(
                                NOT_EXISTS_ID,
                                NOT_EXISTS_ID
                        )))
                .hasSize(1);
    }
}