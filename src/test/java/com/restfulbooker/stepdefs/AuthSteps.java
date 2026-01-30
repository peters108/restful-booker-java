package com.restfulbooker.stepdefs;

import com.restfulbooker.client.AuthClient;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.model.request.AuthRequest;
import com.restfulbooker.hooks.TestHooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Cucumber step definitions for auth scenarios.
 * Uses {@link AuthClient} for API calls and {@link ScenarioContext} to store the response.
 */
public class AuthSteps {

    private static final String AUTH_REQUEST_KEY = "AUTH_REQUEST";

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "password123";

    private final AuthClient authClient = new AuthClient();

    @Given("I have valid credentials")
    public void iHaveValidCredentials() {
        AuthRequest request = new AuthRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);
        getContext().setContext(AUTH_REQUEST_KEY, request);
    }

    @Given("I have credentials with username {string} and password {string}")
    public void iHaveCredentialsWithUsernameAndPassword(String username, String password) {
        AuthRequest request = new AuthRequest(username, password);
        getContext().setContext(AUTH_REQUEST_KEY, request);
    }

    @When("I send a POST request to create token")
    public void iSendAPostRequestToCreateToken() {
        AuthRequest request = (AuthRequest) getContext().getContext(AUTH_REQUEST_KEY);
        Assert.assertNotNull(request, "Credentials must be set in a previous step");
        Response response = authClient.createToken(request);
        getContext().setResponse(response);
    }


    @Then("the response contains a valid token")
    public void theResponseContainsAValidToken() {
        Response response = getAuthResponse();
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/token-response-schema.json"));
    }

    @Then("the token is a non-empty string")
    public void theTokenIsANonEmptyString() {
        Response response = getAuthResponse();
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token);
        Assert.assertFalse(token.isBlank(), "Token must be a non-empty string");
    }

    private ScenarioContext getContext() {
        return TestHooks.getScenarioContext();
    }

    private Response getAuthResponse() {
        Response response = getContext().getResponse();
        Assert.assertNotNull(response, "No auth response found; ensure the request step ran first");
        return response;
    }
}
