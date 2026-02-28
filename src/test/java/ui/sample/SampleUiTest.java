package ui.sample;

import api.models.CreateUserResponse;
import com.codeborne.selenide.Selenide;
import jupiter.annotation.User;
import jupiter.annotation.meta.WebTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ui.BaseUITest;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

@Disabled
@WebTest
public class SampleUiTest extends BaseUITest {
    @Test
    void sampleUiTest(@User CreateUserResponse user) {
        System.out.println("================");
        System.out.println(user);
        Selenide.open("/changes");
        $("[data-test-title='TeamCity']").should(visible);
        sleep(200);
    }

    @Test
    void sampleUiTest1(@User CreateUserResponse user) {
        System.out.println("================");
        System.out.println(user);
        Selenide.open("/changes");
        $("[data-test-title='TeamCity']").should(visible);
        sleep(200);
    }

    @Test
    void sampleUiTest2(@User CreateUserResponse user) {
        System.out.println("================");
        System.out.println(user);
        Selenide.open("/changes");
        $("[data-test-title='TeamCity']").should(visible);
        sleep(200);
    }

    @Test
    void sampleUiTest3(@User CreateUserResponse user) {
        System.out.println("================");
        System.out.println(user);
        Selenide.open("/changes");
        $("[data-test-title='TeamCity']").should(visible);
        sleep(200);
    }
    @Test
    void sampleUiTest4(@User CreateUserResponse user) {
        System.out.println("================");
        System.out.println(user);
        Selenide.open("/changes");
        $("[data-test-title='TeamCity']").should(visible);
        sleep(200);
    }

    @Test
    void noLogin() {
        Selenide.open("/changes");
        $("[data-test-title='TeamCity']").shouldNot(visible);
        sleep(200);
    }
}
