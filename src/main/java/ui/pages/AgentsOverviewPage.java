package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import ui.component.Popup;
import com.codeborne.selenide.SelenideElement;
import common.data.AgentsPageTexts;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AgentsOverviewPage extends AuthorizedPage<AgentsOverviewPage> {
    @Override
    public String url() {
        return "/agents/overview";
    }

    private final ElementsCollection agents =
            $$("div[class*='ExpandableAgentPool-module__expandable'] [data-test='agent']");
    private final SelenideElement unauthorizeButton = $("[data-test-authorize-agent]");
    private final SelenideElement authorizeStatus = $("[data-agent-authorization-status]");

    private final Popup popup = new Popup();
    private final AgentsSidebar agentsSidebar = new AgentsSidebar();

    public AgentsOverviewPage clickAgentToggle(String agentName) {
        agents.find(text(agentName)).$("[data-test='ring-toggle']").click();
        return this;
    }

    public Popup openDisablePopup(String agentName) {
        clickAgentToggle(agentName);
        return popup.shouldBeOpened();
    }

    public AgentsOverviewPage disableAgent(String agentName, String comment) {
        openDisablePopup(agentName)
                .addComment(comment)
                .submit();
        return this;
    }

    public AgentsOverviewPage openAgentDetailsPageFromSidebar(String poolName, String agentName) {
        agentsSidebar.clickAgent(poolName, agentName);
        return this;
    }

    public Popup openUnauthorizeAgentPopup() {
        unauthorizeButton.should(text(AgentsPageTexts.UNAUTHORIZE_BUTTON.getText())).click();
        return popup.shouldBeOpened();
    }

    public AgentsOverviewPage unauthorizeAgent(String comment) {
        openUnauthorizeAgentPopup()
                .addComment(comment)
                .submit();
        authorizeStatus.should(text(AgentsPageTexts.UNAUTHORIZED_STATUS.getText()));
        return this;
    }
}