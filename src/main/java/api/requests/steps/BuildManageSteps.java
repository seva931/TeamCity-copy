package api.requests.steps;

import api.models.CreateBuildTypeRequest;
import api.models.CreateBuildTypeResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.generators.RandomModelGenerator;
import io.qameta.allure.Step;

import java.util.List;

public class BuildManageSteps {

    public record CreateBuildTypeResult(CreateBuildTypeRequest request, CreateBuildTypeResponse response) {
    }

    @Step("Создать случайный build type в проекте '{projectId}'")
    public static CreateBuildTypeResult createBuildType(String projectId) {
        CreateBuildTypeRequest createBuildTypeRequest = RandomModelGenerator.builder(CreateBuildTypeRequest.class)
                .withProjectId(projectId)
                .build();

        CreateBuildTypeResponse createBuildTypeResponse = new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .post(createBuildTypeRequest);

        return new CreateBuildTypeResult(createBuildTypeRequest, createBuildTypeResponse);
    }

    @Step("Получить список всех build types")
    public static List<CreateBuildTypeResponse> getAllBuildTypes() {
        return new ValidatedCrudRequester<CreateBuildTypeResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.BUILD_TYPES,
                ResponseSpecs.requestReturnsOk())
                .getAllBuildTypes(CreateBuildTypeResponse[].class);
    }
}