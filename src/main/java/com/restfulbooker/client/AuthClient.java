package com.restfulbooker.client;

import com.restfulbooker.model.request.AuthRequest;
import io.restassured.response.Response;

/**
 * Encapsulates all Auth-related API calls.
 * Uses {@link RestClient} for the actual request.
 */
public final class AuthClient {

    private final RestClient restClient = new RestClient();

    /**
     * Requests an auth token with the given credentials.
     * Caller asserts on the response (e.g. status, body) for success or error cases.
     *
     * @param authRequest username and password for authentication
     * @return the raw response for status and body assertions
     */
    public Response createToken(AuthRequest authRequest) {
        return restClient.send("POST", "/auth", authRequest);
    }
}
