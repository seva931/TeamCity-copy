package api.specs;

import configs.Config;
import api.models.CreateUserResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;
import java.util.List;

public class RequestSpecs {

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .addFilters(
                        List.of(new RequestLoggingFilter(),
                                new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("BaseUrl") + Config.getProperty("api"));
    }

    public static RequestSpecBuilder withBasicAuth(CreateUserResponse user) {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((user.getUsername() + ":" + user.getTestData().getPassword())
                                .getBytes()));
    }

    public static RequestSpecBuilder withAdminBasicAuth() {
        return defaultRequestBuilder()
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((Config.getProperty("admin.login") + ":" + Config.getProperty("admin.password"))
                                .getBytes()));
    }

    public static RequestSpecification authAsUser(CreateUserResponse userResponse, ContentType type) {
        return defaultRequestBuilder()
                .setContentType(type)
                .setAccept(type)
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((userResponse.getUsername() + ":" + userResponse.getTestData().getPassword())
                                .getBytes()))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return defaultRequestBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((username + ":" + password)
                                .getBytes()))
                .build();
    }

    public static RequestSpecification authAsUser(CreateUserResponse user) {
        return authAsUser(user.getUsername(), user.getTestData().getPassword());
    }

    public static RequestSpecification adminSpec() {
        return authAsUser(Config.getProperty("admin.login"), Config.getProperty("admin.password"));
    }
}
