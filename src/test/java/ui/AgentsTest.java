package ui;

import api.models.Agent;
import api.models.CreateUserResponse;
import api.requests.steps.AgentSteps;
import jupiter.annotation.User;
import jupiter.annotation.WithAgent;
import jupiter.annotation.meta.WebTest;
import jupiter.extension.AgentExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.AgentsOverviewPage;

import static com.codeborne.selenide.logevents.SelenideLogger.step;

@WebTest
@ExtendWith(AgentExtension.class)
public class AgentsTest extends BaseUITest {

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @Test
    void userCanDisableAgent(@User CreateUserResponse user, Agent[] agents) {
        String agentName = agents[0].getName();
        AgentsOverviewPage page = new AgentsOverviewPage();

        step("Открыть страницу Agents", page::open);

        step("Отключить агента " + agentName, () ->
                page.disableAgent(agentName, "Disable for maintenance"));

        boolean isEnabled = Boolean.TRUE.equals(
                step("Проверить через API, что агент не авторизован", () ->
                        AgentSteps.getAgentByName(user, agentName).isEnabled()));

        softly.assertThat(isEnabled)
                .as("Поле enabled")
                .isFalse();
    }

    @WithAgent(configKeys = {"teamcity.agent.1.name"})
    @Test
    void userCanUnauthorizeAgent(@User CreateUserResponse user, Agent[] agent) {
        String agentName = agent[0].getName();
        String agentPool = agent[0].getPool().getName();

        AgentsOverviewPage page = new AgentsOverviewPage();

        step("Открыть страницу Agents", page::open);

        step("Открыть страницу агента" + agentName + " из пула " + agentPool + " из Sidebar", () ->
                page.openAgentDetailsPageFromSidebar(agentPool, agentName));

        step("Отключить агента " + agentName, () ->
                page.unauthorizeAgent("Unauthorized agent comment"));

        boolean isAuthorized = Boolean.TRUE.equals(
                step("Проверить по API, что агент неавторизован",
                        () -> AgentSteps.getAgentByName(user, agentName).isAuthorized()));

        softly.assertThat(isAuthorized)
                .as("Поле authorized")
                .isFalse();
    }
}
