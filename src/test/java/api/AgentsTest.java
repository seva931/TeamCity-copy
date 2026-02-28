package api;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.AgentSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.data.AtributesOfResponse;
import common.data.RoleId;
import io.restassured.http.ContentType;
import jupiter.annotation.User;
import jupiter.annotation.WithAgent;
import jupiter.annotation.meta.ApiTest;
import jupiter.extension.AgentExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ApiTest
@ExtendWith(AgentExtension.class)
public class AgentsTest extends BaseTest {

    private static final long NON_EXISTENT_AGENT_ID = 999_999L;

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @Test
    void shouldProvideListOfAvailableAgents(@User CreateUserResponse user) {
        AgentsResponse response = new ValidatedCrudRequester<AgentsResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get();

        softly.assertThat(response.getAgent())
                .as("Agent не пустой")
                .isNotNull();
        softly.assertThat(response.getCount())
                .as("Поле count")
                .isGreaterThanOrEqualTo(0);
        softly.assertThat(response.getAgent())
                .as("количество agents и поле count")
                .hasSize(response.getCount());
    }

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void shouldDisableOrEnableAgentById(
            String bodyMessage,
            String responseText,
            @User CreateUserResponse user,
            Agent[] agents) {
        long agentId = agents[0].getId();

        String response = new CrudRequester(
                RequestSpecs.withBasicAuth(user)
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, bodyMessage)
                .extract().asString();

        softly.assertThat(response)
                .as("текст в запросе и текст в ответе совпадают")
                .isEqualTo(responseText);

        AgentResponse agentById = AgentSteps.getAgentById(user, agentId);
        softly.assertThat(agentById.isEnabled())
                .as("Поле enabled")
                .isEqualTo(Boolean.parseBoolean(responseText));
    }

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @ParameterizedTest
    @CsvSource({"false,false", "true,true"})
    void shouldAuthorizeOrUnauthorizeAgentById(
            String bodyMessage,
            String responseText,
            @User CreateUserResponse user,
            Agent[] agents) {

        long agentId = agents[0].getId();

        String response = new CrudRequester(
                RequestSpecs.withBasicAuth(user)
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_AUTHORIZED,
                ResponseSpecs.requestReturnsOk()
        ).put(agentId, bodyMessage)
                .extract().asString();

        softly.assertThat(response)
                .as("текст в запросе и текст в ответе совпадают")
                .isEqualTo(responseText);

        AgentResponse agentById = AgentSteps.getAgentById(user, agentId);

        softly.assertThat(agentById.isAuthorized())
                .as("Поле enabled")
                .isEqualTo(Boolean.parseBoolean(responseText));
    }

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @Test
    void shouldProvideInfoAboutAgentById(
            @User CreateUserResponse user,
            Agent[] agents) {
        long agentId = agents[0].getId();

        AgentResponse response = new ValidatedCrudRequester<AgentResponse>(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS_ID,
                ResponseSpecs.requestReturnsOk()
        ).get(agentId);

        assertThat(response)
                .as("Поля id и name")
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "name")
                .isEqualTo(agents[0]);
    }

    @WithAgent()
    @Test
    void shouldReturnListOfUnauthorizedAgents(
            @User CreateUserResponse user,
            Agent[] agents) {
        Agent unauthAgent = agents[0];
        AgentSteps.unauthorizeAgent(unauthAgent.getId());
        Agent authAgent = agents[1];
        AgentSteps.authorizeAgent(authAgent.getId());

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "authorized:false")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит unauthorized агента")
                .contains(unauthAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит authorized агента")
                .doesNotContain(authAgent);
    }

    @WithAgent()
    @Test
    void shouldReturnListOfAuthorizedAgents(
            @User CreateUserResponse user,
            Agent[] agents) {
        Agent unauthAgent = agents[0];
        AgentSteps.unauthorizeAgent(unauthAgent.getId());
        Agent authAgent = agents[1];
        AgentSteps.authorizeAgent(authAgent.getId());

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "authorized:true")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит unauthorized агента")
                .contains(authAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит authorized агента")
                .doesNotContain(unauthAgent);
    }

    @WithAgent()
    @Test
    void shouldReturnListOfEnabledAgents(
            @User CreateUserResponse user,
            Agent[] agents) {
        Agent disabledAgent = agents[0];
        AgentSteps.disableAgent(disabledAgent.getId());
        Agent enabledAgent = agents[1];
        AgentSteps.enableAgent(enabledAgent.getId());

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "enabled:true")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит enabled агента")
                .contains(enabledAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит disabled агента")
                .doesNotContain(disabledAgent);
    }

    @WithAgent()
    @Test
    void shouldReturnListOfDisabledAgents(
            @User CreateUserResponse user,
            Agent[] agents) {
        Agent disabledAgent = agents[0];
        AgentSteps.disableAgent(disabledAgent.getId());
        Agent enabledAgent = agents[1];
        AgentSteps.enableAgent(enabledAgent.getId());

        AgentsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .addQueryParam("locator", "enabled:false")
                        .setAccept(ContentType.JSON)
                        .setContentType(ContentType.JSON)
                        .build(),
                Endpoint.AGENTS,
                ResponseSpecs.requestReturnsOk()
        ).get().extract().as(AgentsResponse.class);

        softly.assertThat(response.getAgent())
                .as("Содержит disabled агента")
                .contains(disabledAgent);
        softly.assertThat(response.getAgent())
                .as("Не содержит enabled агента")
                .doesNotContain(enabledAgent);
    }

    @Test
    void shouldReturnNotFoundForNonexistentAgent(@User CreateUserResponse user) {
        ErrorsResponse response = new CrudRequester(
                RequestSpecs.authAsUser(user),
                Endpoint.AGENTS_ID,
                ResponseSpecs.notFound()
        ).get(NON_EXISTENT_AGENT_ID)
                .extract().as(ErrorsResponse.class);

        assertThat(response.getErrors())
                .hasSize(1)
                .filteredOn(e ->
                        e.getMessage().equals(AtributesOfResponse
                                .NO_AGENT_CAN_BE_FOUND_BY_ID
                                .getFormatedText(NON_EXISTENT_AGENT_ID)))
                .hasSize(1);
    }

    @Test
    void shouldNotBeAbleToEnableAgentWithoutPermissions(
            @User(role = RoleId.PROJECT_VIEWER) CreateUserResponse user) {
        long agentId = AdminSteps.getDefaultAgentId();

        ErrorsResponse response = new CrudRequester(
                RequestSpecs
                        .withBasicAuth(user)
                        .setContentType(ContentType.TEXT)
                        .setAccept(ContentType.TEXT)
                        .build(),
                Endpoint.AGENTS_ID_ENABLED,
                ResponseSpecs.forbidden()
        ).put(agentId, "true").extract().as(ErrorsResponse.class);

        assertThat(response.getErrors())
                .hasSize(1)
                .filteredOn(e ->
                        e.getMessage().equals(AtributesOfResponse
                                .YOU_DO_NOT_HAVE_ENABLE_DISABLE_AGENTS_ASSOCIATED_WITH_PROJECT_PERMISSION_FOR_POOL_DEFAULT
                                .getMessage()))
                .hasSize(1);
    }
}
