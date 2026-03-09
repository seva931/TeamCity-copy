package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.containsString;

public class ResponseSpecs {

    private ResponseSpecs() {
    }

    public static ResponseSpecification requestReturnsOk() {
        return ok();
    }

    public static ResponseSpecification ok() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification noContent() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NO_CONTENT)
                .build();
    }

    public static ResponseSpecification created() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification notFound() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .build();
    }

    public static ResponseSpecification InternalServerError() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .build();
    }

    public static ResponseSpecification notFoundWithErrorText(String errorText) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .expectBody(containsString(errorText))
                .build();
    }

    public static ResponseSpecification badRequestWithErrorText(String errorText) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody("errors[0].additionalMessage", containsString(errorText))
                .build();
    }

    public static ResponseSpecification forbiddenWithErrorText(String errorText) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody("errors[0].additionalMessage", containsString(errorText))
                .build();
    }


    public static ResponseSpecification badRequest() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

    public static ResponseSpecification forbidden() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

    public static ResponseSpecification deletesQuietly() {
        return new ResponseSpecBuilder()
                .expectStatusCode(
                        Matchers.anyOf(
                                Matchers.is(HttpStatus.SC_OK),
                                Matchers.is(HttpStatus.SC_NO_CONTENT),
                                Matchers.is(HttpStatus.SC_NOT_FOUND)))
                .build();
    }
}