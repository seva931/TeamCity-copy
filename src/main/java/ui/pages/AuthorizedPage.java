package ui.pages;

import ui.component.LeftNavigationMenu;

public abstract class AuthorizedPage<T extends AuthorizedPage<T>> extends BasePage<T> {

    protected LeftNavigationMenu leftNavigationMenu = new LeftNavigationMenu();

    public LeftNavigationMenu leftMenu() {
        return leftNavigationMenu;
    }
}