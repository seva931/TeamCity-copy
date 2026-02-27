package ui;

import api.models.CreateUserResponse;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import jupiter.annotation.meta.WithBuild;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.pages.ProjectsPage;

@WebTest
@WithBuild
public class LeftNavigationMenuTest extends BaseUITest {

    @DisplayName("Позитивный тест: проверка навигационного меню")
    @Test
    public void leftNavigationMenuItemsShouldBeVisible(@User CreateUserResponse user) {
        new ProjectsPage()
                .open()
                .leftMenu()
                .checkAllButtonsVisible();
    }
}