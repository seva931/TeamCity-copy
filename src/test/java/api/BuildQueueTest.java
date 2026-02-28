package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AgentSteps;
import api.requests.steps.BuildQueueSteps;
import api.requests.steps.BuildStepsSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.QueryParamData;
import common.generators.RandomModelGenerator;
import io.restassured.http.ContentType;
import jupiter.annotation.Build;
import jupiter.annotation.Project;
import jupiter.annotation.User;
import jupiter.annotation.meta.ApiTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@ApiTest
@WithBuild
public class BuildQueueTest extends BaseTest {

    //TODO вынести teardown в отдельный метод и @AfterEach

    @Test
    void userCanGetInfoAboutAllQueuedBuildWithEmptyQueue(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {
        BuildQueueListResponse response = new CrudRequester(
                RequestSpecs.withBasicAuth(user)
                        .setContentType(ContentType.JSON)
                        .setAccept(ContentType.JSON)
                        .addQueryParam(QueryParamData.LOCATOR.getName(), "buildType:(id:" + build.getId() + ")")
                        .build(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(BuildQueueListResponse.class);

        softly.assertThat(response.getCount()).isZero();
        softly.assertThat(response.getBuild()).isEmpty();
    }

    @Test
    void userCanGetInfoAboutSingleQueuedBuildById(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {
        BuildStepsSteps.addEndlessStep(user, build);
        BuildQueueResponse buildInQueue = BuildQueueSteps.queueBuild(build, user);
        Long idOfBuildInQueue = buildInQueue.getId();

        BuildQueueListResponse response = new CrudRequester(
                RequestSpecs.withBasicAuth(user)
                        .setContentType(ContentType.JSON)
                        .setAccept(ContentType.JSON)
                        .addQueryParam(QueryParamData.LOCATOR.getName(), idOfBuildInQueue)
                        .build(),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(BuildQueueListResponse.class);

        softly.assertThat(response).isNotNull();
        softly.assertThat(response.getCount()).isNotZero();
        softly.assertThat(response.getBuild())
                .hasSize(1)
                .extracting(QueuedBuild::getId, QueuedBuild::getBuildTypeId, QueuedBuild::getState)
                .contains(tuple(buildInQueue.getId(), buildInQueue.getBuildTypeId(), buildInQueue.getState()));

        //teardown
        BuildQueueSteps.deleteQueuedBuildQuietly(idOfBuildInQueue, user);
    }

    @Test
    void userCanQueueRegularBuild(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {

        BuildStepsSteps.addEndlessStep(user, build);
        BuildQueueRequest request = BuildQueueRequest.of(build.getId());

        BuildQueueResponse response = new ValidatedCrudRequester<BuildQueueResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).post(request);

        BuildQueueListResponse allQueuedBuilds = BuildQueueSteps.getAllQueuedBuilds(user);

        softly.assertThat(allQueuedBuilds.getBuild())
                .extracting(QueuedBuild::getId, QueuedBuild::getBuildTypeId, QueuedBuild::getState)
                .contains(tuple(response.getId(), response.getBuildTypeId(), response.getState()));

        //teardown
        BuildQueueSteps.deleteQueuedBuildQuietly(response.getId(), user);
    }

    @Test
    void userCanCancelStartedBuild(
            @User CreateUserResponse user,
            @Project ProjectResponse project,
            @Build CreateBuildTypeResponse build
    ) {
        BuildQueueResponse buildQueueResponse = BuildQueueSteps.queueEndlessBuild(build, user);
        CancelBuildRequest request = RandomModelGenerator.generate(CancelBuildRequest.class);
        request.setReaddIntoQueue(false);

        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILDS_LOCATOR,
                ResponseSpecs.ok()
        ).post(buildQueueResponse.getId(), request);

        //TODO проверка в запущеных билдах и в очереди

        //teardown
        BuildQueueSteps.deleteQueuedBuildQuietly(buildQueueResponse.getId(), user);
    }
}