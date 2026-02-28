package api.requests.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import io.restassured.http.ContentType;

public class BuildQueueSteps {

    public static BuildQueueResponse queueBuild(CreateBuildTypeResponse build, CreateUserResponse user) {
        BuildQueueRequest body = BuildQueueRequest.of(build.getId());

        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).post(body)
                .extract()
                .as(BuildQueueResponse.class);
    }

    public static void deleteQueuedBuild(long queueId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.noContent()
        ).delete(queueId);
    }

    public static void deleteQueuedBuildQuietly(long queueId, CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE_ID,
                ResponseSpecs.deletesQuietly()
        ).delete(queueId);
    }

    public static void deleteAllQueuedBuilds(CreateUserResponse user) {
        new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.deletesQuietly()
        ).delete();
    }

    public static BuildQueueListResponse getAllQueuedBuilds(CreateUserResponse user) {
        return new CrudRequester(
                RequestSpecs.authAsUser(user, ContentType.JSON),
                Endpoint.BUILD_QUEUE,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(BuildQueueListResponse.class);
    }

    public static BuildQueueResponse queueEndlessBuild(CreateBuildTypeResponse build, CreateUserResponse user) {

        BuildStepsSteps.addEndlessStep(user, build);
        return BuildQueueSteps.queueBuild(build, user);
    }
}