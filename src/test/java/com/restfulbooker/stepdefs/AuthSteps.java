package com.restfulbooker.stepdefs;

import com.restfulbooker.config.ConfigurationManager;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.model.request.AuthRequest;
import com.restfulbooker.hooks.TestHooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.testng.Assert;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Cucumber step definitions for auth scenarios.
 * Credentials are set in context; request is sent via "I send \"POST /auth\" request" (CommonSteps).
 * Valid/admin credentials come from configuration (override via system properties or env for CI).
 */
public class AuthSteps {

    @Given("I have valid credentials")
    public void iHaveValidCredentials() {
        var config = ConfigurationManager.getConfiguration();
        setAuthContext(new AuthRequest(config.authUsername(), config.authPassword()));
    }

    @Given("I have invalid credentials")
    public void iHaveInvalidCredentials() {
        setAuthContext(new AuthRequest("invalid", "invalid"));
    }

    @Given("I have admin credentials")
    public void iHaveAdminCredentials() {
        var config = ConfigurationManager.getConfiguration();
        setAuthContext(new AuthRequest(config.authUsername(), config.authPassword()));
    }

    @Given("I have credentials with username {string} and password {string}")
    public void iHaveCredentialsWithUsernameAndPassword(String username, String password) {
        setAuthContext(new AuthRequest(username, password));
    }

    private void setAuthContext(AuthRequest request) {
        getContext().setContext(ScenarioContext.REQUEST_BODY, request);
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
