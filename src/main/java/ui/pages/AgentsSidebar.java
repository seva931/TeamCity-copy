package ui.pages;

import com.codeborne.selenide.SelenideElement;
import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

import static com.codeborne.selenide.Selenide.$;

public class AgentsSidebar extends BaseComponent<AgentsSidebar>{
    public enum SidebarState {
        PINNED,
        COLLAPSED,
        EXPANDED
    }

    private static final String COLLAPSED_CLASS_MARKER = "SidebarPanel-module__collapsed";
    private static final String PIN_BUTTON_SELECTOR = "button[aria-label='Pin sidebar'], button[aria-label='Unpin sidebar']";

    public AgentsSidebar(SelenideElement self) {
        super(self);
    }

    public AgentsSidebar() {
        super($("[data-test='sidebar']"));
    }

    public SidebarState state() {
        self.shouldBe(visible, Duration.ofSeconds(10));

        if (isCollapsed()) {
            return SidebarState.COLLAPSED;
        }

        String pinButtonLabel = self.$(PIN_BUTTON_SELECTOR).getAttribute("aria-label");
        if ("Unpin sidebar".equals(pinButtonLabel)) {
            return SidebarState.PINNED;
        }

        if ("Pin sidebar".equals(pinButtonLabel)) {
            return SidebarState.EXPANDED;
        }

        throw new IllegalStateException("Unknown sidebar state. Pin button aria-label: " + pinButtonLabel);
    }

    public boolean isPinned() {
        return state() == SidebarState.PINNED;
    }

    public boolean isCollapsed() {
        String classes = self.getAttribute("class");
        return classes != null && classes.contains(COLLAPSED_CLASS_MARKER);
    }

    public boolean isExpanded() {
        return state() == SidebarState.EXPANDED;
    }

    public AgentsSidebar ensureOpened() {
        self.shouldBe(visible);
        if(isCollapsed()) {
            self.$("button[aria-label='Pin sidebar']").click();
        }

        self.$("[data-test='sidebar-search']").shouldBe(visible);
        return this;
    }

    public AgentsSidebar clickAgent(String poolName, String agentName) {
        ensureOpened();
        ensureExpandAgentPool(poolName);
        self.$("[aria-label='"+agentName+"']")
                .shouldBe(visible)
                .click();

        return this;
    }

    public AgentsSidebar ensureExpandAgentPool(String poolName) {
        SelenideElement aPool = self.$$("[data-test='agent-pool']").findBy(text(poolName));
        SelenideElement expandBtn = aPool.$("[data-test='expand-button']");
        SelenideElement collapseBtn = aPool.$("[data-test='collapse-button']");
        if(expandBtn.exists()) {
            expandBtn.click();
            collapseBtn.shouldBe(visible);
        }
        return this;
    }
}
