package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    SERVER("/server", BaseModel.class, BaseModel.class),
    USERS("/users", CreateUserRequest.class, CreateUserResponse.class),
    USERS_ID("/users/id:%s", BaseModel.class, User.class),
    USERS_USERNAME("/users/username:%s", BaseModel.class, CreateUserResponse.class),
    PROJECTS("/projects", CreateProjectRequest.class, ProjectResponse.class),
    PROJECT_ID("/projects/id:%s", BaseModel.class, ProjectResponse.class),
    BUILD_TYPES("/buildTypes", CreateBuildTypeRequest.class, CreateBuildTypeResponse.class),
    BUILD_TYPES_ID("/buildTypes/id:%s", BaseModel.class, GetInfoBuildTypeResponse.class),
    BUILD_TYPES_ID_STEPS("/buildTypes/id:%s/steps", AddBuildStepRequest.class, BuildStepsResponse.class),
    BUILD_TYPES_ID_STEPS_ID("/buildTypes/id:%s/steps/%s", BaseModel.class, BaseModel.class),
    VCS_ROOTS("/vcs-roots", BaseModel.class, AllVcsRootsResponse.class),
    VCS_ROOTS_ID("/vcs-roots/id:%s", AddNewRootRequest.class, AddNewRootResponse.class),
    AGENTS("/agents", BaseModel.class, AgentsResponse.class),
    AGENTS_LOCATOR("/agents/%s", BaseModel.class, AgentsResponse.class),
    AGENTS_ID("/agents/id:%s", BaseModel.class, AgentResponse.class),
    AGENTS_ID_ENABLED("/agents/id:%s/enabled", BaseModel.class, BaseModel.class),
    AGENTS_ID_AUTHORIZED("/agents/id:%s/authorized", BaseModel.class, BaseModel.class),
    PROJECT_NAME("/projects/id:%s/parameters/name", BaseModel.class, BaseModel.class),
    USERS_ID_PERMISSIONS("/users/id:%s/permissions", BaseModel.class, PermissionsResponse.class),
    USERS_ID_ROLES("/users/id:%s/roles", BaseModel.class, Role.class),
    BUILD_QUEUE("/buildQueue", BaseModel.class, BuildQueueResponse.class),
    BUILD_QUEUE_ID("/buildQueue/id:%s", BaseModel.class, BaseModel.class),
    BUILDS_LOCATOR("/builds/id:%s", BaseModel.class, BaseModel.class);




    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    public String getFormatedUrl(Object... args) {
        return String.format(url, args);
    }
}
