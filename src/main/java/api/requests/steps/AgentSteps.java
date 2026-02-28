package api.requests.steps;

import api.models.AgentResponse;
import api.models.AgentsResponse;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.QueryParamData;
import io.restassured.http.ContentType;

public class AgentSteps {

    public static AgentsResponse getAgents(CreateUserResponse user) {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static AgentsResponse getAgents() {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static AgentsResponse getAllAgents() {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.withAdminBasicAuth()
                        .addQueryParam(QueryParamData.LOCATOR.getName(), "defaultFilter:false,authorized:any,enabled:any,connected:any")
                        .addQueryParam(QueryParamData.FIELDS.getName(), "agent(id,name,typeId,enabled,connected,authorized,ip,pool(id,name),build(id),environment,)")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static AgentsResponse getAllEnabledAgents() {
        return new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.withAdminBasicAuth()
                        .addQueryParam(QueryParamData.LOCATOR.getName(), "enabled:true")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();
    }

    public static String disableAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "false").extract().asString();
    }

    public static String enableAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "true").extract().asString();
    }

    public static String authorizeAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_AUTHORIZED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "true").extract().asString();
    }

    public static String unauthorizeAgent(long agentId) {
        return new CrudRequester(
                RequestSpecs.withAdminBasicAuth()
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_AUTHORIZED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, "false").extract().asString();
    }

    public static AgentResponse getAgentById(CreateUserResponse user, long agentId) {

        return new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(user.getUsername(), user.getTestData().getPassword()),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(agentId);
    }

    public static AgentResponse getAgentByName(CreateUserResponse user, String agentName) {

        return new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS_LOCATOR,
                ResponseSpecs.requestReturnsOk()
        ).get(agentName).extract().as(AgentResponse.class);
    }
}
