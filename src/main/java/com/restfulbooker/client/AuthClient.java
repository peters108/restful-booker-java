package com.restfulbooker.client;

import com.restfulbooker.model.request.AuthRequest;
import com.restfulbooker.specs.RequestSpecBuilder;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Encapsulates all Auth-related API calls.
 */
public final class AuthClient {

    /**
     * Requests an auth token with the given credentials.
     * Caller asserts on the response (e.g. status, body) for success or error cases.
     *
     * @param authRequest username and password for authentication
     * @return the raw response for status and body assertions
     */
    public Response createToken(AuthRequest authRequest) {
        return given()
                .spec(RequestSpecBuilder.build())
                .body(authRequest)
                .when()
                .post("/auth")
                .then()
                .extract()
                .response();
    }
}
