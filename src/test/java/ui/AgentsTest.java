package ui;

import api.models.Agent;
import api.models.CreateUserResponse;
import configs.Config;
import jupiter.annotation.User;
import jupiter.annotation.WithAgent;
import jupiter.annotation.meta.WebTest;
import jupiter.extension.AgentExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.AgentsOverviewPage;

@WebTest
@ExtendWith(AgentExtension.class)
public class AgentsTest extends BaseUITest {

    @Disabled("Падает, скотина")
    @WithAgent
    @Test
    void userCanDisableAgent(@User CreateUserResponse user, Agent[] agents) {
        String agentName = Config.getProperty("teamcity.agent.1.name");
        new AgentsOverviewPage()
                .open()
                .disableAgent(agentName, "Disable for maintenance");

        //TODO добавить проверку на апи что агент disabled
        //TODO возвращать в состояние enabled (добавить @WithAgent аннотацию)
    }
}
