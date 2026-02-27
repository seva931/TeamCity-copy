package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import ui.component.Popup;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class AgentsOverviewPage extends AuthorizedPage<AgentsOverviewPage> {
    @Override
    public String url() {
        return "/agents/overview";
    }

    private final ElementsCollection agents =
            $$("div[class*='ExpandableAgentPool-module__expandable'] [data-test='agent']");
    private final Popup popup = new Popup();

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
}